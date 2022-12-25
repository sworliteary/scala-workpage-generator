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
      val genres = database.getGenres
      Map(_s.path -> html.index(genres).toString)
    }
  }
}
