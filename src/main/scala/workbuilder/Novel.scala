package workbuilder

import scala.math.Ordering
import scala.io.Source
import java.nio.file.Path
import java.nio.file.Paths

case class NovelInfoJson(
    title: String,
    date: Option[String],
    tag: Option[Seq[String]],
    files: Option[Seq[String]],
    caption: Option[String]
) {
  def toNovelInfo =
    NovelInfo(
      title,
      caption,
      tag.fold(Seq.empty[Tag])(_.map(Tag(_))),
      files.getOrElse[Seq[String]](Seq("text.txt")),
      date.map(Date(_))
    )
}

case class NovelInfo(
    title: String,
    caption: Option[String],
    tag: Seq[Tag],
    files: Seq[String],
    date: Option[Date]
)

case class Novel(
    path: Path,
    info: NovelInfo,
    genre: Genre
) {
  def htmlTag = s"""
<div class="work_info">
<h2><a href="/${outputPath}">${info.title}</a></h2>
${info.caption.fold("")(c => s"<p class=\"caption\">${c.replaceAll("\n", "<br>")}</p>")}
<div class="tags">${info.tag.map(_.htmlTag).mkString(" ")}</div>
${info.date.fold("")(date => s"""<p class="date">${date.year}/${date.month}/${date.day}</p>""")}
<hr>
</div>"""

  def outputPath: String = genre.path + "/" + Util.camelCaseToSnakeCase(path.getFileName().toString())
}

object NovelPageGenerator extends workbuilder.PageGenerator[Novel] {
  def generate(source: Novel, database: Database): Map[Path, String] = {
    val length = source.info.files.length
    val toc =
      if (length == 1) (_: Int) => ""
      else { (i: Int) =>
        s"""<div class="toc">${(1 to length - 1)
            .map(j => if (i == j) s"${j}" else s"<a href=\"${fileName(j)}\">${j}</a>")
            .mkString(" ")}
            </div>"""
      }
    def toHtmlText(text: String): String = text
      .split("\n\n")
      .map(l =>
        s"\n<p>${l.replaceAll("(.)\n", "$1<br/>\n").replaceAll("｜([^《]*)《([^》]*)》", "<ruby>$1<rt>$2</rt></ruby>")}</p>\n"
      )
      .mkString
    source.info.files.zipWithIndex
      .map((f, i) => {
        val text = Source.fromFile(source.path.resolve(f).toString()).mkString
        val path = Paths.get(source.outputPath).resolve(fileName(i))
        val html = Util.htmlPage(
          source.info.title,
          s"""<h1 class="title">${source.info.title}</h1>
          |${toc(i)}
          |<hr>
          |<div class="text">${toHtmlText(text)}</div>
          |${toc(i)}
          |<hr>
          |<div class="info">
          |  <p><a href="/${source.genre.path}">${source.genre.name} 作品一覧</a></p>
          |  <p><a href="/">Top</a><p>
          |</div>""".stripMargin
        )
        (path -> html)
      })
      .toMap
  }
  private def fileName(index: Int) = if (index == 0) "index.html" else s"$index.html"
}
