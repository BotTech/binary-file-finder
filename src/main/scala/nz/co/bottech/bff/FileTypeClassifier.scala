package nz.co.bottech.bff

import java.nio.file.Path

import scala.util.Try

trait FileTypeClassifier {

  def fileType(file: Path): Try[FileType]
}
