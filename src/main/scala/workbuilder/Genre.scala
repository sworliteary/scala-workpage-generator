package workbuilder

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import workbuilder.html

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

object Genre {
  implicit object GenrePageGenerator extends PageGenerator[Genre] {
    def generate(source: Genre, database: Database): Map[Path, String] = {
      def path = Paths.get(source.path).resolve("index.html")
      database.getNovels.filter(_.genre == source).sortBy(_.date).reverse match {
        case Nil      => Map()
        case _ @works => Map(path -> html.novels(works, HEADER + source.name, false).toString)
      }
    }
  }
  val HEADER = "◇ "
}
