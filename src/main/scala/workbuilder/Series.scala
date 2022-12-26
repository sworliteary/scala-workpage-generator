package workbuilder

import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import scala.io.Source

case class SeriesJson(
    name: String,
    description: String
) {
  def toSeries(genre: Genre, file: File) =
    Series(
      name,
      Paths.get(genre.path).resolve(Util.camelCaseToSnakeCase(file.getParentFile().getName())).toString(),
      description,
      genre,
      file.getParentFile()
    )
}

case class Series(
    name: String,
    path: String,
    description: String,
    genre: Genre,
    directory: File
)

object Series {
  def fromFile(genre: Genre, f: File) =
    decode[SeriesJson](Source.fromFile(f).mkString).toOption.map(_.toSeries(genre, f))

  implicit object SeriesGenerator extends PageGenerator[Series] {
    def generate(source: Series, database: Database): Map[Path, String] = {
      val novels = database.getNovels.filter(_.series.exists(_ == source)).sortBy(_.genre.directory.getName())
      println(novels)
      Map(
        Paths.get(source.path).resolve("index.html") -> html
          .novels(
            novels,
            HEADER + source.name,
            ShowNovelInfoOption(showGenre = true, showSeries = false),
            Some(source.description)
          )
          .toString
      )
    }
  }
  val HEADER = "✒ "
}
