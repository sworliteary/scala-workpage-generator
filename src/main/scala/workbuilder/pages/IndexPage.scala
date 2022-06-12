package workbuilder.pages

import java.nio.file.Paths
import workbuilder.PageGenerator
import workbuilder.Database
import workbuilder.Util
import workbuilder.Genre
import java.nio.file.Path

sealed trait IndexPage {
  def path = Paths.get("index.html")
}

object IndexPageObject extends IndexPage

object IndexPage {
  implicit object IndexPageGenerator extends PageGenerator[IndexPage] {
    def generate(_s: IndexPage, database: Database): Map[Path, String] = {
      val genres = database.getGenres
      def genreTag(g: Genre) = s"<li><a href=\"${g.path}\">${g.name}</a></li>"
      val html = Util.htmlPage(
        "サヨナラボイジャー",
        s"""<h2>サイト概要</h2>
          |<p>藤谷光の作品サイトです。</p>
          |<ul>
          |  <li><a href="https://sayonara.voyage">Webサイト</a></li>
          |  <li><a href="https://twitter.com/sworliteary">Twitter</a></li>
          |</ul>
          |<h2>作品</h2>
          |<h3>オリジナル</h3>
          |<ul>
          |${genres.filter(!_.is_fan_fiction).map(genreTag).mkString("\n")}
          |</ul>
          |<h3>二次創作</h3>
          |<ul>
          |${genres.filter(_.is_fan_fiction).map(genreTag).mkString("\n")}
          |</ul>""".stripMargin
      )
      Map(_s.path -> html)
    }
  }
}
