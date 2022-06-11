package workbuilder

import scala.collection.mutable
import java.nio.file.Path

// 恐怖！！！全情報保持クラス
class Database(val repository: Path) {

  def addGenre(genre: Genre*) = {
    genres.appendAll(genre)
  }

  def addNovel(novel: Novel*) = {
    novels.appendAll(novel)
    tags.addAll(novel.map(_.info.tag).flatten)
  }

  def getGenres = genres.toList

  def getNovels = novels.toList

  def getTags = tags.toList

  private lazy val genres: mutable.ListBuffer[Genre] = mutable.ListBuffer.empty
  private lazy val novels: mutable.ListBuffer[Novel] = mutable.ListBuffer.empty
  private lazy val tags: mutable.Set[Tag] = mutable.HashSet.empty
}
