package workbuilder.pages

import workbuilder.Database
import workbuilder.PageGenerator
import workbuilder.html

import java.nio.file.Path
import java.nio.file.Paths

sealed trait RecentlyPage {
  def path = Paths.get("recent.html")
}

object RecentlyPageObject extends RecentlyPage

object RecentlyPage {
  implicit object RecentlyPageGenerator extends PageGenerator[RecentlyPage] {
    def generate(source: RecentlyPage, database: Database): Map[Path, String] = {
      val novels = database.getNovels.sortBy(_.date).reverse.slice(0, 10)
      var r = html.novels(novels, "最近の投稿", workbuilder.ShowNovelInfoOption(showGenre = true), None).toString
      Map(source.path -> r)
    }
  }
}
