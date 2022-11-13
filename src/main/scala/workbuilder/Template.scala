package workbuilder

object Template {

  def makeTitle(titleParts: String) = if (titleParts.isEmpty()) "サヨナラボイジャー" else s"$titleParts | サヨナラボイジャー"
  def htmlPage(title: String, body: String) =
    s"""<!DOCTYPE html>        
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>${makeTitle(title)}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Dancing+Script:wght@700&display=swap" />
  <link rel="stylesheet" href="/index.css" />
</head>
<body>
<div class="header">
  <h1><a href="/">Sayonara VoyagE</a></h1>
  <div class="menu">
    <span><a href="/recent.html">最近の投稿</a></span>
    <span><a href="/tags/">タグ一覧</a></span>
  </div>
</div>
$body
<div class="footer">
  <p class="copywrite">2022 Hikari Hujiya</p>
  <p><a href="https://forms.gle/h8L5MZdDbPatCq2G8">作品への感想</a> / <a href="mailto:shnovels@gmail.com">お問い合わせ</a></p>
</div>
</body>
</html>  
"""
}
