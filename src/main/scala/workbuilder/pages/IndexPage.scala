package workbuilder.pages

import java.nio.file.Paths
import workbuilder.PageGenerator
import workbuilder.Database
import workbuilder.Genre
import workbuilder.html
import java.nio.file.Path

sealed trait IndexPage {
  def path = Paths.get("index.html")
}

object IndexPageObject extends IndexPage

object IndexPage {
  implicit object IndexPageGenerator extends PageGenerator[IndexPage] {
    def generate(_s: IndexPage, database: Database): Map[Path, String] = {
      val genres = database.getGenres.filter(v => database.getNovels.filter(_.genre == v).length > 0)
      val novels = database.getNovels.sortBy(_.date).reverse.slice(0, 3)
      Map(_s.path -> html.index(genres, novels, workbuilder.ShowNovelInfoOption(showGenre = true)).toString)
    }
  }
}
