package workbuilder

import scala.math.Ordering
import scala.io.Source
import java.nio.file.Path
import java.nio.file.Paths

import workbuilder.html

import play.twirl.api.Html

case class NovelInfoJson(
    title: String,
    date: Option[String],
    tag: Option[Seq[String]],
    files: Option[Seq[String]],
    caption: Option[String],
    draft: Option[Boolean]
) {
  def toNovel(path: Path, genre: Genre) =
    Novel(
      path,
      genre,
      title,
      caption,
      tag.fold(Seq.empty[Tag])(_.map(Tag(_))),
      files.getOrElse[Seq[String]](Seq("text.txt")),
      date.map(Date(_)),
      draft.isDefined
    )
}

case class Novel(
    path: Path,
    genre: Genre,
    title: String,
    caption: Option[String],
    tag: Seq[Tag],
    files: Seq[String],
    date: Option[Date],
    draft: Boolean
) {
  // TODO: showGenreとshowCaptionを適切にパラメータ化すること
  def htmlTag(showGenre: Boolean = false, showCaption: Boolean = true) =
    s"""
    |<div class="work_info">
    |  <h2><a href="/${outputPath}">${title}</a></h2>
    |  <div class="info">
    |    ${
        if (showCaption && caption.isDefined)
          s"<div class=\"caption\"><p>${caption.get.replaceAll("\n", "<br>")}</p></div>"
        else ""
      }
    |    ${date.fold("")(date => s"""<p class="date">${date.year}/${f"${date.month}%02d"}/${f"${date.day}%02d"}</p>""")}
    |    ${if (showGenre) s"<p class=\"genre\"><a href=\"/${genre.path}\">${genre.name}</a></p>" else ""}
    |    <div class="tags">${tag.map(_.htmlTag()).mkString}</div>

    |  </div>
    |<hr>
    |</div>""".stripMargin

  def outputPath: String = genre.path + "/" + Util.camelCaseToSnakeCase(path.getFileName().toString())

  def hasTag(t: Tag) = tag.contains(t)
}

object Novel {
  implicit object NovelPageGenerator extends workbuilder.PageGenerator[Novel] {
    def generate(source: Novel, database: Database): Map[Path, String] = {
      val length = source.files.length
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
      val date = source.date.fold("")(date =>
        s"""<p class="date">${date.year}/${f"${date.month}%02d"}/${f"${date.day}%02d"}</p>"""
      )
      val caption =
        source.caption.fold("")(c => s"<div class=\"caption\"><p>${c.replaceAll("\n", "<br>")}</p></div>")
      source.files.zipWithIndex
        .map((f, i) => {
          val text = Source.fromFile(source.path.resolve(f).toString()).mkString
          val path = Paths.get(source.outputPath).resolve(fileName(i))
          val pageTitle = if (length == 1) s"${source.title}" else s"${source.title} (${i + 1})"
          var html2 = html
            .baseof(pageTitle)(
              Html(s"""<h1 class="title">${source.title}</h1>
          |<div class="work_header_info">
          |  ${caption}
          |  ${date}
          |  <p><a href="/${source.genre.path}">${source.genre.name}</a></p>
          |  <div class="tag">${source.tag.map(_.htmlTag()).mkString}</div>
          |</div>
          |${toc(i)}
          |<div class="text">${toHtmlText(text)}</div>
          |${toc(i)}
          |""".stripMargin)
            )
            .toString()
          (path -> html2)
        })
        .toMap
    }
    private def fileName(index: Int) = if (index == 0) "index.html" else s"${index + 1}.html"
  }
}
