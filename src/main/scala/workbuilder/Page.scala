package workbuilder

import java.nio.file.Path

trait Page {
  def generate(): String
}

trait PageGenerator[T] {
  def generate(souce: T): (Path, String)
}
