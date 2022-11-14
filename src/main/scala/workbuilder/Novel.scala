package workbuilder

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.math.Ordering
import scala.io.Source
import java.nio.file.Path
import java.nio.file.Paths
import io.circe.Encoder.AsArray.importedAsArrayEncoder
import io.circe.Encoder.AsObject.importedAsObjectEncoder
import io.circe.Encoder.AsRoot.importedAsRootEncoder

import workbuilder.Genre._

case class NovelInfoJson(
    title: String,
    date: Option[String],
    tag: Option[Seq[String]],
    files: Option[Seq[String]],
    caption: Option[String],
    draft: Option[Boolean]
) {
  def toNovel(path: Path, genre: Genre) = {
    val texts = files.getOrElse(Seq("text.txt")).map(f => Source.fromFile(path.resolve(f).toString()).mkString)
    Novel(
      path,
      genre,
      title,
      caption,
      tag.fold(Seq.empty[Tag])(_.map(Tag(_))),
      texts,
      date.map(Date(_)),
      draft.isDefined
    )
  }
}

case class Novel(
    path: Path,
    genre: Genre,
    title: String,
    caption: Option[String],
    tag: Seq[Tag],
    texts: Seq[String],
    date: Option[Date],
    draft: Boolean
) {
  // TODO: showGenreとshowCaptionを適切にパラメータ化すること
  def htmlTag(showGenre: Boolean = false, showCaption: Boolean = true) =
    s"""
    |<div class="work_info">
    |  <h2><a href="/${outputPath}">${title}</a></h2>
    |  <div class="info">
    |    ${date.fold("")(date =>
        s"""<p class="date">投稿日: ${date.year}/${f"${date.month}%02d"}/${f"${date.day}%02d"}</p>"""
      )}
    |    ${if (showGenre) s"<p class=\"genre\">ジャンル: <a href=\"/${genre.path}\">${genre.name}</a></p>" else ""}
    |    <div class="tags">${tag.map(_.htmlTag()).mkString}</div>
    |    ${
        if (showCaption && caption.isDefined)
          s"<div class=\"caption\"><p>${caption.get.replaceAll("\n", "<br>")}</p></div>"
        else ""
      }
    |  </div>
    |<hr>
    |</div>""".stripMargin

  def outputPath: String = genre.path + "/" + Util.camelCaseToSnakeCase(path.getFileName().toString())

  def hasTag(t: Tag) = tag.contains(t)
}

object Novel {
  implicit val novelEncoder: Encoder[Novel] =
    Encoder.forProduct7("path", "genre", "title", "caption", "tag", "texts", "date")(s =>
      (s.path.toString(), s.genre, s.title, s.caption.getOrElse(""), s.tag.map(_.name), s.texts, s.date)
    )
  implicit object NovelPageGenerator extends workbuilder.PageGenerator[Novel] {
    def generate(source: Novel, database: Database): Map[Path, String] = {
      Map(Paths.get(source.outputPath + ".json") -> source.asJson.toString)
      /*
      val toc =
        if (length == 1) (_: Int) => ""
        else { (i: Int) =>
          s"""<div class="toc">page: ${(0 to length - 1)
              .map(j =>
                if (i == j) s"<span style=\"text-decoration:underline;\">${j + 1}</span>"
                else s"<a href=\"${fileName(j)}\">${j + 1}</a>"
              )
              .mkString(" ")}
            </div>"""
        }
      def toHtmlText(text: String): String = text
        .split("\n\n+")
        .map(l =>
          s"\n<p>${l.replaceAll("(.)\n", "$1<br/>\n").replaceAll("｜([^《]*)《([^》]*)》", "<ruby>$1<rt>$2</rt></ruby>")}</p>\n"
        )
        .mkString
      source.files.zipWithIndex
        .map((f, i) => {
          val text = Source.fromFile(source.path.resolve(f).toString()).mkString
          val path = Paths.get(source.outputPath).resolve(fileName(i))
          val pageTitle = if (length == 1) s"${source.title}" else s"${source.title} (${i + 1})"
          val html = Template.htmlPage(
            pageTitle,
            s"""<h1 class="title">${source.title}</h1>
          |<div class="work_header_info">
          |  ${source.date.fold("")(date =>
                s"""<p class="date">投稿日: ${date.year}/${f"${date.month}%02d"}/${f"${date.day}%02d"}</p>"""
              )}
          |  <p>ジャンル: <a href="/${source.genre.path}">${source.genre.name}</a></p>
          |  <div class="tag">${source.tag.map(_.htmlTag()).mkString}</div>
          |</div>
          |${toc(i)}
          |<div class="text">${toHtmlText(text)}</div>
          |${toc(i)}
          |""".stripMargin
          )
          (path -> html)
        })
        .toMap
       */
    }
    private def fileName(index: Int) = if (index == 0) "index.html" else s"${index + 1}.html"
  }
}
