package workbuilder

trait NovelRepository {
  def getByGenre(genre: Genre): Seq[Novel]
  def getByTag(tag: Tag): Seq[Novel]
  def getAll(): Seq[Novel]
}
