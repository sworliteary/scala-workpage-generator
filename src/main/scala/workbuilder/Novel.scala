package workbuilder

import scala.math.Ordering

case class NovelInfoJson(
    title: String,
    date: Option[String],
    tag: Option[Array[String]],
    files: Option[Array[String]],
    caption: Option[String]
)

case class NovelInfo(
    title: String,
    caption: Option[String],
    tag: Seq[Tag],
    date: Date
)

case class Novel(
    text: Seq[String],
    path: String,
    info: NovelInfo,
    genre: Genre
) {
  def toHtmls(): Seq[String] = {
    val toc =
      if (text.length == 1) { (i: Int) => "" }
      else { (i: Int) =>
        s"""<div class="toc">${(1 to text.length - 1)
            .map(j =>
              if (i == j) s"${j}"
              else s"<a href=\"${if (j == 1) "index" else s"${j}"}.html\">${j}</a>"
            )
            .mkString(" ")}
            <hr></div>"""
      }
    def toHtmlText(text: String): String = text
      .split("\n\n")
      .map(l =>
        s"\n<p>${l.replaceAll("(.)\n", "$1<br/>\n").replaceAll("｜([^《]*)《([^》]*)》", "<ruby>$1<rt>$2</rt></ruby>")}</p>\n"
      )
      .mkString

    text.zipWithIndex.map((text, i) =>
      Util.htmlPage(
        info.title,
        s"""
<h1 class="title">${info.title}</h1>
${toc(i)}
<div class="text">${toHtmlText(text)}
</div>
<hr>
${toc(i)}
<div class="info">
  <p><a href="${genre.path}">${genre.name}</a></p>
  <!--<p>${info.tag.map(_.name).mkString(",")}</p>-->
  <p><a href="/">Top</a><p>
</div>"""
      )
    )
  }

  def outputPath(): String = genre.path + "/" + Util.camelCaseToSnakeCase(path)
}
