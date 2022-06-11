package workbuilder

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

case class GenreJson(
    name: String,
    is_fan_fiction: Boolean
)

sealed trait Genre {
  def path: String
  def name: String
  def is_fan_fiction: Boolean
  def directory: File
}

case class Original(f: File) extends Genre {
  override def path = "original"
  override def name = "オリジナル"

  override def is_fan_fiction: Boolean = false

  override def directory = f
}

case class Fanfiction(_name: String, _path: String, f: File) extends Genre {
  override def path = "fan_fiction/" + Util.camelCaseToSnakeCase(_path)
  override def name: String = _name
  override def is_fan_fiction: Boolean = true

  override def directory = f
}

object GenrePageGenerator extends PageGenerator[Genre] {
  def generate(source: Genre, database: Database): Map[Path, String] = {
    def path = Paths.get(source.path).resolve("index.html")
    def works = database.getNovels.filter(_.genre == source).sortBy(_.info.date).reverse
    val html = Util.htmlPage(
      source.name + " | sayonara-voyage",
      s"""<h1>${source.name}</h1>
      |${works.map(_.htmlTag).mkString}
      |<div class="info">
      |<p><a href="/">Top</a><p>
      |</div>
      """.stripMargin
    )
    Map(path -> html)
  }
}
