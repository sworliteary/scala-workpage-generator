package workbuilder

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import java.net.URLEncoder

import workbuilder.html

case class Tag(name: String) {
  // 実装移すときはここを再実装する必要あり
  def path = "tags/" + name.hashCode().toHexString
  def htmlTag(num: Option[Int] = None) =
    num match {
      case Some(i) =>
        s"<span class=\"tag\"><a href=\"/$path/\">#<span class=\"tag_content\">$name</span> ($i)</a></span>"
      case None => s"<span class=\"tag\"><a href=\"/$path/\">#<span class=\"tag_content\">$name</span></a></span>"
    }

}

object Tag {
  implicit object TagPageGenerator extends PageGenerator[Tag] {
    def generate(source: Tag, database: Database): Map[Path, String] = {
      val novels = database.getNovels.filter(_.hasTag(source)).sortBy(_.date).reverse
      val path = Paths.get(source.path).resolve("index.html")
      Map(path -> html.novels(novels, s"${HEADER}${source.name}", true).toString)
    }
  }
  val HEADER = "#"
}
