package workbuilder

import scala.collection.mutable
import java.nio.file.Path

// 恐怖！！！全情報保持クラス
class Database(showDraft: Boolean = false) {
  def addGenre(genre: Genre*) = {
    genres.appendAll(genre)
  }

  def addNovel(novel: Novel*) = {
    novels.appendAll(novel)
    tags.addAll(novel.map(_.tag).flatten)
  }

  def addSeries(s: Series*) = series.appendAll(s)

  def getGenres = genres.toList

  def getNovels = novels.toList.filter(!_.draft || showDraft)

  def getTags = tags.toList

  def getSeries = series.toList

  private val genres: mutable.ListBuffer[Genre] = mutable.ListBuffer.empty
  private val novels: mutable.ListBuffer[Novel] = mutable.ListBuffer.empty
  private val tags: mutable.Set[Tag] = mutable.HashSet.empty
  private val series: mutable.ListBuffer[Series] = mutable.ListBuffer.empty
}
