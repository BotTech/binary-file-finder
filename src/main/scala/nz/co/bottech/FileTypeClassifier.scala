package nz.co.bottech

import java.nio.file.Path

import scala.util.Try

trait FileTypeClassifier {

  def fileType(file: Path): Try[FileType]
}
