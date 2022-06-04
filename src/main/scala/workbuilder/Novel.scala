package workbuilder

case class NovelInfo(
    title: String,
    caption: Option[String],
    tag: Seq[Tag]
)

case class Novel(
    text: String,
    path: String,
    info: NovelInfo,
    genre: Genre
) {
  /*
  override def generate(): String = {
    val context =
      Context("title" -> Value.of(info.title), "text" -> Value.of(text))
    val template = mustache.parse("<h1>{{title}}</h1><p>{{text}}</p>")
    mustache.render(template.toOption.get)(context)
  }
   */
}
