package workbuilder

object Main {
  def main(args: Array[String]) {
    val novel = Novel(
      "これが本文です。これが本文です。",
      NovelInfo("タイトルです", "/text", Some("aaaa"), Seq()),
      Original
    )
    println(novel.generate())
  }
}
