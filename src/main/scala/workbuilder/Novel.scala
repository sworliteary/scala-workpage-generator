package workbuilder

import scala.math.Ordering

case class NovelInfoJson(
    title: String,
    date: Option[String],
    tag: Option[Array[String]],
    files: Option[Array[String]],
    caption: Option[String]
)

case class Date(year: Int, month: Int, day: Int) {}

object Date {
  def apply(raw: String): Date = {
    val p = raw.split("/").map(_.toInt)
    Date(p(0), p(1), p(2))
  }

  def apply(): Date = {
    Date(2014, 6, 1) // Twitter始めたのがこのあたりらしい
  }
  implicit object DateOrdering extends Ordering[Date] {
    override def compare(a: Date, b: Date): Int = {
      if (a.year != b.year) a.year.compare(b.year);
      if (a.month != b.month) a.month.compare(b.month);
      a.day.compare(b.month);
    }
  }
}

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
    text.zipWithIndex.map((text, i) =>
      s"""
      ${Util.htmlHeader(info.title)}
      <h1 class="title">${info.title}</h1>
      ${toc(i)}
      <div class="text">${text
          .split("\n\n")
          .map(l =>
            // このコードがここに存在してるのいくらなんでもやばすぎる
            s"<p>${l.replaceAll("\n", "<br/>").replaceAll("｜([^《]*)《([^》]*)》", "<ruby>$1<rt>$2</rt></ruby>")}</p>"
          )
          .mkString}
      </div>
      <hr>
      ${toc(i)}
      <div class="info">
        <p><a href="${genre.path}">${genre.name}</a></p>
        <!--<p>${info.tag.map(_.name).mkString(",")}</p>-->
        <p><a href="/">Top</a><p>
      </div>
      ${Util.htmlFooter}
      """
    )
  }
  def outputPath(): String = genre.path + "/" + Util.camelCaseToSnakeCase(path)
  /*
  override def generate(): String = {
    val context =
      Context("title" -> Value.of(info.title), "text" -> Value.of(text))
    val template = mustache.parse("<h1>{{title}}</h1><p>{{text}}</p>")
    mustache.render(template.toOption.get)(context)
  }
   */
}
