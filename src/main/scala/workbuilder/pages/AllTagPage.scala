package workbuilder.pages

import java.nio.file.Path
import java.nio.file.Paths

import workbuilder.PageGenerator
import workbuilder.Database
import workbuilder.Template
import workbuilder.html

sealed trait AllTagPage

object AllTagPageObject extends AllTagPage

object AllTagPage {
  implicit object AllTagPageGenerator extends PageGenerator[AllTagPage] {
    def generate(source: AllTagPage, database: Database): Map[Path, String] = {
      Map(Paths.get("tags/index.html") -> html.alltag(database.getTags, database.getNovels).toString)
    }
  }
}
