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
import workbuilder.pages.AboutPageO
import workbuilder.html
import com.typesafe.scalalogging.LazyLogging

object Main extends LazyLogging {
  def listFilesRecursive(dir: File, fileName: String): Seq[File] = {
    val these = dir.listFiles.filter(_.getPath.endsWith(fileName))
    these ++ dir.listFiles
      .filter(_.isDirectory)
      .flatMap(d => listFilesRecursive(d, fileName))
  }

  def genPage[T, U >: T](baseDir: Path, database: Database)(source: T*)(using generator: PageGenerator[U]) = {
    source.map(s => generator.generate(s, database)).flatten.map((p, s) => writeFile(baseDir)(p, s))
  }

  def writeFile(baseDir: Path)(path: Path, text: String) = {
    val p = baseDir.resolve(path)
    if (!p.getParent().toFile().exists()) {
      Files.createDirectories(p.getParent())
    }
    Files.write(p, text.getBytes(StandardCharsets.UTF_8))
  }

  def getGenres(repository: File) =
    listFilesRecursive(repository, "genre.json")
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

  def getNovels(genre: Genre) =
    listFilesRecursive(genre.directory, "work.json")
      .map(f =>
        decode[NovelInfoJson](Source.fromFile(f).mkString).toOption
          .map(_.toNovel(f.getParentFile().toPath(), genre))
      )
      .flatten
      .filter(!_.draft)

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      logger.error("repository path is not defined.")
      return
    }

    val novelRepository = new File(args.head)
    val db = new Database

    val genres = getGenres(novelRepository)
    db.addGenre(genres: _*)
    logger.info(s"added genres (${genres.length})")

    val works = genres
      .map(getNovels)
      .flatten
    db.addNovel(works: _*)
    logger.info(s"added novels (${works.length})")

    val outDir = Paths.get("public")
    FileUtils.copyDirectory(File("static"), outDir.toFile(), true)
    genPage(outDir, db)(genres: _*)
    genPage(outDir, db)(works: _*)
    genPage(outDir, db)(db.getTags: _*)
    genPage(outDir, db)(AllTagPageObject)
    genPage(outDir, db)(RecentlyPageObject)
    genPage(outDir, db)(IndexPageObject)
    genPage(outDir, db)(AboutPageO)
  }
}
