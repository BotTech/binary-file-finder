package nz.co.bottech.bff

import java.nio.file.Path

import scala.collection.immutable.TreeMap

final case class FileReport(files: Map[Path, FileType]) {

  def print(): Unit = {
    files.foreach {
      case (file, result) => println(s"$file: $result")
    }
  }

  def sort: FileReport = {
    FileReport(TreeMap[Path, FileType](files.toSeq: _*))
  }

  def groupByType: TypeReport = {
    val typeMap = files.toSeq.groupBy {
      case (_, result) => result
    }.mapValues(_.map {
      case (file, _) => file
    })
    TypeReport(typeMap)
  }
}
