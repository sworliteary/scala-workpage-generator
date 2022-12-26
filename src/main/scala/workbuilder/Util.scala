package workbuilder

object Util {
  def camelCaseToSnakeCase(name: String): String = {
    name.zipWithIndex
      .map((v, i) =>
        if (v == '-') "_"
        else if (!v.isUpper) v.toString()
        else if (i == 0 || name(i - 1) == '_' || name(i - 1) == '/') v.toLower.toString()
        else "_" + v.toLower.toString()
      )
      .mkString
  }
}
