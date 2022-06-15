package workbuilder.pages

import java.nio.file.Path
import java.nio.file.Paths

import workbuilder.PageGenerator
import workbuilder.Database
import workbuilder.Template

sealed trait AllTagPage

object AllTagPageObject extends AllTagPage

object AllTagPage {
  implicit object AllTagPageGenerator extends PageGenerator[AllTagPage] {
    def generate(source: AllTagPage, database: Database): Map[Path, String] = {
      val path = Paths.get("tags/index.html")
      val html = Template.htmlPage(
        "tags | サヨナラボイジャー",
        s"""
        |<h1>Tags</h1>
        |${database.getTags.map(_.htmlTag).mkString}""".stripMargin
      )
      Map(path -> html)
    }
  }
}
