package workbuilder.pages

import workbuilder.PageGenerator
import workbuilder.Database
import java.nio.file.Path
import java.nio.file.Paths
import workbuilder.Template

sealed trait RecentlyPage {
  def path = Paths.get("recent.html")
}

object RecentlyPageObject extends RecentlyPage

object RecentlyPage {
  implicit object RecentlyPageGenerator extends PageGenerator[RecentlyPage] {
    def generate(source: RecentlyPage, database: Database): Map[Path, String] = {
      val novels = database.getNovels.sortBy(_.date).reverse.slice(0, 10)
      val html = Template.htmlPage(
        "最近の投稿 | サヨナラボイジャー",
        s"""<h1>最近の投稿</h1>
           |${novels.map(_.htmlTag(true).mkString).mkString}""".stripMargin
      )
      Map(source.path -> html)
    }
  }
}
