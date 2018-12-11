package nz.co.bottech

import java.nio.file.FileVisitResult._
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{SimpleFileVisitor, _}

class FileTypeVisitor(exclude: Path => Boolean, reporter: FileReporter) extends SimpleFileVisitor[Path] {

  override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
    if (exclude(dir)) {
      SKIP_SUBTREE
    } else {
      CONTINUE
    }
  }

  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
    if (!exclude(file)) {
      reporter.record(file)
    }
    CONTINUE
  }
}
