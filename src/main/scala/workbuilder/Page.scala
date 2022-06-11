package workbuilder

import java.nio.file.Path

trait PageGenerator[T] {
  def generate(source: T, database: Database): Map[Path, String]
}
