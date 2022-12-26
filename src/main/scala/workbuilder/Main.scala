package workbuilder

import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.FileUtils
import workbuilder.html
import workbuilder.pages.AboutPageO
import workbuilder.pages.AllTagPageObject
import workbuilder.pages.IndexPageObject
import workbuilder.pages.RecentlyPageObject

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import scala.io.Source

import collection.JavaConverters._
import math.Ordered.orderingToOrdered

object Main extends LazyLogging {
  def listFilesRecursive(dir: File, fileName: String): Seq[File] = listFilesRecursive(dir, fileName, Seq.empty)

  def listFilesRecursive(dir: File, fileName: String, ignoreDir: Seq[File]): Seq[File] = {
    val these = dir.listFiles.filter(_.getPath.endsWith(fileName))
    these ++ dir.listFiles
      .filter(z => z.isDirectory && !ignoreDir.contains(z))
      .flatMap(d => listFilesRecursive(d, fileName))
  }

  def genPage[T, U >: T](baseDir: Path, database: Database)(source: T*)(using generator: PageGenerator[U]) = {
    source.map(s => generator.generate(s, database)).flatten.map((p, s) => writeFile(baseDir)(p, s))
  }

  def writeFile(baseDir: Path)(path: Path, text: String) = {
    val p = baseDir.resolve(path)
    logger.debug(s"write file: ${p.toString()}")
    if (!p.getParent().toFile().exists()) {
      Files.createDirectories(p.getParent())
    }
    Files.write(p, text.getBytes(StandardCharsets.UTF_8))
  }

  def getGenres(repository: File) =
    listFilesRecursive(repository, "genre.json")
      .map(Genre.fromFile)
      .flatten

  def getSeries(genre: Genre) =
    listFilesRecursive(genre.directory, "series.json")
      .map(Series.fromFile(genre, _))
      .flatten

  def getNovels(genre: Genre, series: Seq[Series]): Seq[Novel] =
    listFilesRecursive(genre.directory, "work.json", series.map(_.directory))
      .map(f => Novel.fromFile(genre, f))
      .flatten
      .filter(!_.draft)
      .concat(
        series
          .map(s => listFilesRecursive(s.directory, "work.json").map(f => Novel.fromFile(s.genre, f, Some(s))).flatten)
          .flatten
      )

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      logger.error("repository path is not defined.")
      return
    }

    val novelRepository = new File(args.head)
    val db = new Database

    val genres = getGenres(novelRepository)
    db.addGenre(genres: _*)
    logger.info(s"added genres (${genres.length})")

    val series = genres.map(getSeries).flatten
    db.addSeries(series: _*)
    logger.info(s"added series (${series.length})")

    val works = genres
      .map(g => getNovels(g, db.getSeries.filter(_.genre == g)))
      .flatten

    db.addNovel(works: _*)
    logger.info(s"added novels (${works.length})")

    val outDir = Paths.get("public")
    FileUtils.copyDirectory(File("static"), outDir.toFile(), true)
    genPage(outDir, db)(genres: _*)
    genPage(outDir, db)(series: _*)
    genPage(outDir, db)(works: _*)
    genPage(outDir, db)(db.getTags: _*)
    genPage(outDir, db)(AllTagPageObject)
    genPage(outDir, db)(RecentlyPageObject)
    genPage(outDir, db)(IndexPageObject)
    genPage(outDir, db)(AboutPageO)
  }
}
