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

object Main {
  def listFilesRecursive(dir: File, fileName: String): Array[File] = {
    val these = dir.listFiles.filter(_.getPath.endsWith(fileName))
    these ++ dir.listFiles
      .filter(_.isDirectory)
      .flatMap(d => listFilesRecursive(d, fileName))
  }

  def writeFile(path: Path, text: String, outputDir: Path) = {
    val p = outputDir.resolve(path)
    if (!p.getParent().toFile().exists()) {
      Files.createDirectories(p.getParent())
    }
    Files.write(p, text.getBytes(StandardCharsets.UTF_8))
  }

  def main(args: Array[String]): Unit = {
    val novelRepository = new File(args.head)
    val outputDir = Paths.get(args.head).resolve(".out")
    val db = new Database(Paths.get(novelRepository.getPath()))

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
            decode[NovelInfoJson](Source.fromFile(f).mkString) match {
              case Right(v) => Some(Novel(f.getParentFile().toPath(), v.toNovelInfo, genre))
              case Left(e)  => None
            }
          )
          .flatten
      )
      .flatten
    db.addNovel(works: _*)
    genres.map(g => GenrePageGenerator.generate(g, db)).flatten.map((p, f) => writeFile(p, f, outputDir))
    works.map(w => NovelPageGenerator.generate(w, db)).flatten.map((p, f) => writeFile(p, f, outputDir))
    // index html
    val index = Util.htmlPage(
      "work.sayonara.voyage",
      s"""<h1>work.sayonara.voyage</h1>
<h2>サイト概要</h2>
<p>藤谷光の作品サイトです。</p>
<ul>
  <li><a href="https://sayonara.voyage">Webサイト</a></li>
  <li><a href="https://twitter.com/sworliteary">Twitter</a></li>
</ul>
<h2>作品</h2>
<h3>オリジナル</h3>
<ul>
${genres.filter(!_.is_fan_fiction).map(g => s"<li><a href=\"${g.path}\">${g.name}</a></li>\n").mkString("\n")}
</ul>
<h3>二次創作</h3>
<ul>
${genres.filter(_.is_fan_fiction).map(g => s"<li><a href=\"${g.path}\">${g.name}</a></li>").mkString("\n")}
</ul>"""
    )
    Files.write(outputDir.resolve("index.html"), index.getBytes(StandardCharsets.UTF_8))
  }
}
