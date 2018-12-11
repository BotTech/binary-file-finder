package nz.co.bottech.bff

import java.nio.file.Path

import scala.collection.immutable.TreeMap

final case class TypeReport(types: Map[FileType, Seq[Path]]) {

  def print(): Unit = {
    types.foreach {
      case (result, paths) =>
        println(s"$result:")
        paths.foreach(path => println(s"\t$path"))
    }
  }

  def printTypes(): Unit = {
    types.keys.foreach(println)
  }

  def sort: TypeReport = {
    TypeReport(TreeMap[FileType, Seq[Path]](types.toSeq: _*))
  }
}
