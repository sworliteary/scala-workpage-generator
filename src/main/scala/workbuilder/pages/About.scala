package workbuilder.pages

import workbuilder.PageGenerator
import workbuilder.Database
import java.nio.file.Path
import java.nio.file.Paths

sealed trait AboutPageT

object AboutPageO extends AboutPageT

object AboutPageT {
  implicit object AboutPageGenerator extends PageGenerator[AboutPageT] {
    override def generate(source: AboutPageT, database: Database): Map[Path, String] = {
      Map(Paths.get("about.html") -> workbuilder.html.about().toString)
    }
  }
}
