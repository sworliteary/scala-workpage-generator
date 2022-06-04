package workbuilder

import java.io.File
import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import collection.JavaConverters._
import org.apache.commons.io.FileUtils

object Main {
  def listFilesRecursive(dir: File, fileName: String): Array[File] = {
    val these = dir.listFiles.filter(_.getPath.endsWith(fileName))
    these ++ dir.listFiles
      .filter(_.isDirectory)
      .flatMap(d => listFilesRecursive(d, fileName))
  }

  def main(args: Array[String]): Unit = {
    val path = args.head
    val f = new File(path)
    val genresJson = listFilesRecursive(f, "genre.json")
    val genres = genresJson
      .map(f => {
        val parent = f.getParentFile()
        val raw = FileUtils.readFileToString(f, "utf-8")
        decode[GenreJson](raw) match {
          case Right(some) => {
            if (some.is_fan_fiction) {
              Some(Fanfiction(some.name, parent.getName(), parent))
            } else {
              Some(Original(parent))
            }
          }
          case Left(_) => None
        }
      })
      .flatten
    genres.foreach(v => {
      val works = listFilesRecursive(v.directory, "work.json")
      println(s"--- ${v.name}")
      println(works.map(_.getParent).mkString("\n"))
    })
    println(genres.map(v => s"${v.name}(${v.path})").mkString("\n"))

    val works = listFilesRecursive(f, "work.toml")
    println(works.map(_.getParent).mkString("\n"))

    // println(listFilesRecursive(f, "work.toml").map(v => v.getPath).mkString("\n"))

    /*
    val novel = Novel(
      "これが本文です。これが本文です。",
      NovelInfo("タイトルです", "/text", Some("aaaa"), Seq()),
      Original()
    )
     */
  }
}
