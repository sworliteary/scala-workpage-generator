package workbuilder

import java.io.File
import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import collection.JavaConverters._
import scala.io.Source
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import math.Ordered.orderingToOrdered

object Main {
  def listFilesRecursive(dir: File, fileName: String): Array[File] = {
    val these = dir.listFiles.filter(_.getPath.endsWith(fileName))
    these ++ dir.listFiles
      .filter(_.isDirectory)
      .flatMap(d => listFilesRecursive(d, fileName))
  }

  def main(args: Array[String]): Unit = {
    val path = args.head
    val repository = new File(path)
    val outputBase = repository.getPath() + "/.out"
    val genres = listFilesRecursive(repository, "genre.json")
      .map(f => {
        decode[GenreJson](Source.fromFile(f).mkString) match {
          case Right(some) =>
            if (some.is_fan_fiction)
              Some(Fanfiction(some.name, f.getParentFile().getName(), f.getParentFile()))
            else
              Some(Original(f.getParentFile()))
          case Left(_) => None
        }
      })
      .flatten
    genres.foreach(genre => {
      val works = listFilesRecursive(genre.directory, "work.json")
        .map(f =>
          decode[NovelInfoJson](Source.fromFile(f).mkString) match {
            case Right(v) => {
              val date = v.date.map(Date(_))
              val info = NovelInfo(v.title, v.caption, v.tag.fold(Seq.empty[Tag])(_.map(Tag(_))), date)
              val texts = v.files
                .fold(Array(f.getParent() + "/text.txt"))(_.map(f.getParent() + "/" + _))
                .map(Source.fromFile(_).mkString)
              Some(Novel(texts, Util.camelCaseToSnakeCase(f.getParentFile().getName()), info, genre))
            }
            case Left(e) => None
          }
        )
        .flatten
        .sortWith((a, b) => a.info.date > b.info.date)
      works.foreach(w => {
        val dir = Paths.get(outputBase, w.outputPath())
        if (!Files.exists(dir)) Files.createDirectories(dir)
        w.toHtmls()
          .zipWithIndex
          .foreach((html, i) =>
            Files.write(dir.resolve(if (i == 0) "index.html" else s"$i.html"), html.getBytes(StandardCharsets.UTF_8))
          )
      })
      // genre page
      val dir = Paths.get(outputBase, genre.path)
      if (!Files.exists(dir)) Files.createDirectories(dir)
      val genreHtml = Util.htmlPage(
        genre.name + " | work.sayonara.voyage",
        s"""
        <h1>${genre.name}</h1>
        ${works
            .map(v => {
              s"""
            <div class="work_info">
            <h2><a href=\"${v.path}\">${v.info.title}</a></h2>
            ${v.info.caption.fold("")(c => s"<p class=\"caption\">${c.replaceAll("\n", "<br>")}</p>")}
            <div class="tags">${v.info.tag.map(_.htmlTag).mkString(" ")}</div>
            ${v.info.date.fold("")(date => s"""<p class="date">${date.year}/${date.month}/${date.day}</p>""")}
            <hr>
            </div>
            """
            })
            .mkString}
      <div class="info">
      <p><a href="/">Top</a><p>
      </div>
      """
      )
      Files.write(dir.resolve("index.html"), genreHtml.getBytes(StandardCharsets.UTF_8))

      println(s"--- ${genre.name} (${works.length})")
      println(
        works
          .map(w => s"${w.info.title} (${w.info.tag.mkString(",")})")
          .mkString("\n")
      )
    })

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
    Files.write(Paths.get(outputBase).resolve("index.html"), index.getBytes(StandardCharsets.UTF_8))
  }
}
