package workbuilder

import java.io.File
import collection.JavaConverters._

object Main {
  def listFilesRecursive(dir: File, fileName: String):  Array[File] = {
    val these = dir.listFiles.filter(_.getPath.endsWith(fileName))
    these ++ dir.listFiles.filter(_.isDirectory).flatMap(d => listFilesRecursive(d, fileName))
  }

  def main(args: Array[String]): Unit = {
    val path = args.head
    val f = new File(path)
    val works = listFilesRecursive(f, "work.toml")
    println(works.map(_.getParent).mkString("\n"))
    // println(listFilesRecursive(f, "work.toml").map(v => v.getPath).mkString("\n"))
  
    val novel = Novel(
      "これが本文です。これが本文です。",
      NovelInfo("タイトルです", "/text", Some("aaaa"), Seq()),
      Original
    )
  }
}
