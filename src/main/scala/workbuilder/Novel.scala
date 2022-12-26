package workbuilder

import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import play.twirl.api.Html
import workbuilder.html

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import scala.io.Source
import scala.math.Ordering

case class NovelInfoJson(
    title: String,
    date: Option[String],
    tag: Option[Seq[String]],
    files: Option[Seq[String]],
    caption: Option[String],
    draft: Option[Boolean]
) {
  def toNovel(path: Path, genre: Genre, series: Option[Series] = None) =
    Novel(
      path,
      genre,
      series,
      title,
      caption,
      tag.fold(Seq.empty[Tag])(_.sorted.map(Tag(_))),
      files.getOrElse[Seq[String]](Seq("text.txt")),
      date.map(Date(_)),
      draft.exists(z => z)
    )
}

case class ShowNovelInfoOption(
    showGenre: Boolean = false,
    showCaption: Boolean = true,
    showSeries: Boolean = true
)

case class Novel(
    path: Path,
    genre: Genre,
    series: Option[Series],
    title: String,
    caption: Option[String],
    tag: Seq[Tag],
    files: Seq[String],
    date: Option[Date],
    draft: Boolean
) {
  def infoTag(option: ShowNovelInfoOption) =
    """<div class="info">""" +
      // caption
      (if (option.showCaption && caption.isDefined)
         s"<div class=\"caption\"><p>${caption.get.replaceAll("\n", "<br>")}</p></div>"
       else "")
      // Genre
      + (if (option.showGenre) s"<p class=\"genre\"><a href=\"/${genre.path}\">${Genre.HEADER}${genre.name}</a></p>"
         else "")
      // Series
      + (if (option.showSeries)
           series.fold("")(s => s"""<p class="series"><a href="/${s.path}">${Series.HEADER}${s.name}</a></p>""")
         else "")
      + s"""<div class="tags">${tag.map(_.htmlTag()).mkString}</div>"""
      // date
      + date.fold("")(date => s"""<p class="date">${date.year}/${f"${date.month}%02d"}/${f"${date.day}%02d"}</p>""")
      + "</div>"

  // TODO: showGenreとshowCaptionを適切にパラメータ化すること
  def htmlTag(option: ShowNovelInfoOption) =
    s"""
    |<div class="work_info">
    |  <h3 class="title"><a href="/${outputPath}">${title}</a></h3>
    | ${infoTag(option)}
    | <hr>
    |</div>""".stripMargin

  def outputPath: String =
    genre.path + "/" + Util.camelCaseToSnakeCase(
      path.toString().replaceAllLiterally(genre.directory.toString() + "/", "")
    )

  def hasTag(t: Tag) = tag.contains(t)
}

object Novel {
  def fromFile(genre: Genre, f: File, s: Option[Series] = None) =
    decode[NovelInfoJson](Source.fromFile(f).mkString).toOption
      .map(_.toNovel(f.getParentFile().toPath(), genre, s))

  implicit object NovelPageGenerator extends workbuilder.PageGenerator[Novel] {
    def generate(source: Novel, database: Database): Map[Path, String] = {
      val length = source.files.length
      val toc =
        if (length == 1) (_: Int) => ""
        else { (i: Int) =>
          s"""<div class="toc">${(0 to length - 1)
              .map(j =>
                if (i == j) s"<span style=\"text-decoration:underline;\">${j + 1}</span>"
                else s"<a href=\"${fileName(j)}\">${j + 1}</a>"
              )
              .mkString(" ")} (p)</div>"""
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
          | ${source.infoTag(ShowNovelInfoOption(showGenre = true))}
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
