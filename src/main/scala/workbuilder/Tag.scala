package workbuilder

import java.nio.file.Path
import java.nio.file.Paths

case class Tag(name: String) {
  def path = "tags/" + name
  def htmlTag = s"<span class=\"tag\"><a href=\"/$path\">$name</a></span>"
}

object TagPageGenerator extends PageGenerator[Tag] {
  def generate(source: Tag, database: Database): Map[Path, String] = {
    val novels = database.getNovels.filter(_.hasTag(source)).sortBy(_.date).reverse
    val path = Paths.get(source.path).resolve("index.html")
    val html = Util.htmlPage(
      s"tag: ${source.name} | sayonara-voyage",
      s"""<h1>Tag: ${source.name}</h1>
    |${novels.map(_.htmlTag).mkString}""".stripMargin
    )
    Map(path -> html)
  }
}
