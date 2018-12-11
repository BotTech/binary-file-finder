package nz.co.bottech

import java.nio.file.Path

import scala.concurrent.Future

trait FileReporter {

  def record(file: Path): Unit

  def report: Future[FileReport]
}
