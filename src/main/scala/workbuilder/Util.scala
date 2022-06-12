package workbuilder

object Util {
  def camelCaseToSnakeCase(name: String): String = {
    name.zipWithIndex
      .map((v, i) =>
        if (v == '-') "_"
        else if (!v.isUpper) v.toString()
        else if (i == 0 || name(i - 1) == '_') v.toLower.toString()
        else "_" + v.toLower.toString()
      )
      .mkString
  }
  def htmlPage(title: String, body: String) = s"""<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>${title}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
  <link rel="stylesheet" href="/index.css"/>
</head>
<body>
<div class="header">
  <h1><a href="/">Sayonara VoyagE</a></h1>
</div>
$body
<div class="footer">
  <p class="copywrite">2022 Hikari Hujiya</p>
</div>
</body>
</html>  
"""
}
