package nz.co.bottech.bff

import java.nio.file.FileVisitResult._
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{SimpleFileVisitor, _}

import nz.co.bottech.bff.report.FileReporter

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
      if (reporter.record(file)) {
        CONTINUE
      } else {
        TERMINATE
      }
    }
    CONTINUE
  }
}
