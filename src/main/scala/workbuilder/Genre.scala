package workbuilder

import java.io.File

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
  override def path = "/original"
  override def name = "オリジナル"

  override def is_fan_fiction: Boolean = true

  override def directory = f
}

case class Fanfiction(_name: String, _path: String, f: File) extends Genre {
  override def path = "/fan_fiction/" + Genre.camelCaseToSnakeCase(_path)
  override def name: String = _name
  override def is_fan_fiction: Boolean = true

  override def directory = f
}

object Genre {
  def camelCaseToSnakeCase(name: String): String = {
    name.zipWithIndex
      .map((v, i) =>
        if (!v.isUpper) v.toString()
        else if (i == 0) v.toLower.toString()
        else "_" + v.toLower.toString()
      )
      .mkString
  }
}
