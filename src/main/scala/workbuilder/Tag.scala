package workbuilder

case class Tag(name: String) {
  def path = "/tags/" + name
  def htmlTag = s"<span class=\"tag\"><a href=\"$path\">$name</a></span>"
}
