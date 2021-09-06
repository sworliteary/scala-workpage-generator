package workbuilder

sealed trait Genre {
  def path: String
  def name: String
}

object Original extends Genre {
  override def path = "/original"
  override def name = "オリジナル"
}
case class Fanfiction(_name: String, _path: String) extends Genre {
  override def path = "/fanfiction/" + _path
  override def name: String = _name
}

