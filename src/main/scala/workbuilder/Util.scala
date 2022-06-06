package workbuilder

object Util {
  def camelCaseToSnakeCase(name: String): String = {
    name.zipWithIndex
      .map((v, i) =>
        if (!v.isUpper) v.toString()
        else if (i == 0) v.toLower.toString()
        else "_" + v.toLower.toString()
      )
      .mkString
  }
  def htmlHeader(title: String): String = s"""<!DOCTYPE html>
<html lang="ja">
  <head>
    <meta charset="utf-8" />
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
    <link rel="stylesheet" href="/index.css"/>
  </head>
  <body>
  """

  lazy val htmlFooter: String = s"""
  <p class="copywrite">&copy; 2022 Hikari Hujiya</p>
  </body>
  </html>
  """
}
