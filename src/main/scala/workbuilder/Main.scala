package workbuilder

import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import org.apache.commons.io.FileUtils

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import scala.io.Source

import collection.JavaConverters._
import math.Ordered.orderingToOrdered

import workbuilder.pages.AllTagPageObject
import workbuilder.pages.RecentlyPageObject
import workbuilder.pages.IndexPageObject

object Main {
  def listFilesRecursive(dir: File, fileName: String): Seq[File] = {
    val these = dir.listFiles.filter(_.getPath.endsWith(fileName))
    these ++ dir.listFiles
      .filter(_.isDirectory)
      .flatMap(d => listFilesRecursive(d, fileName))
  }

  def genPage[T, U >: T](baseDir: Path)(database: Database, source: T*)(implicit generator: PageGenerator[U]) = {
    source.map(s => generator.generate(s, database)).flatten.map((p, s) => writeFile(baseDir)(p, s))
  }

  def writeFile(baseDir: Path)(path: Path, text: String) = {
    val p = baseDir.resolve(path)
    if (!p.getParent().toFile().exists()) {
      Files.createDirectories(p.getParent())
    }
    Files.write(p, text.getBytes(StandardCharsets.UTF_8))
  }

  def main(args: Array[String]): Unit = {
    val novelRepository = new File(args.head)
    val db = new Database

    val genres = listFilesRecursive(novelRepository, "genre.json")
      .map(f =>
        decode[GenreJson](Source.fromFile(f).mkString) match {
          case Right(some) =>
            if (some.is_fan_fiction)
              Some(Fanfiction(some.name, f.getParentFile().getName(), f.getParentFile()))
            else
              Some(Original(f.getParentFile()))
          case Left(_) => None
        }
      )
      .flatten
    db.addGenre(genres: _*)
    val works = genres
      .map(genre =>
        listFilesRecursive(genre.directory, "work.json")
          .map(f =>
            decode[NovelInfoJson](Source.fromFile(f).mkString).toOption
              .map(_.toNovel(f.getParentFile().toPath(), genre))
          )
          .flatten
      )
      .flatten
    db.addNovel(works: _*)

    val outDir = Paths.get(".out")
    FileUtils.copyDirectory(File("static"), File(".out"))
    genPage(outDir)(db, works: _*)
    genPage(outDir)(db, genres: _*)
    genPage(outDir)(db, db.getTags: _*)
    genPage(outDir)(db, AllTagPageObject)
    genPage(outDir)(db, RecentlyPageObject)
    genPage(outDir)(db, IndexPageObject)
  }
}
