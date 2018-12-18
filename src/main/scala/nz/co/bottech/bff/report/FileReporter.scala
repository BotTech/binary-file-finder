package nz.co.bottech.bff.report

import java.nio.file.Path

import scala.concurrent.Future

trait FileReporter {

  def record(file: Path): Boolean

  def report: Future[FileReport]
}
