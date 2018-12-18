package nz.co.bottech.bff.report

import java.nio.file.Path

import scala.collection.JavaConverters._
import nz.co.bottech.bff.FileType
import nz.co.bottech.bff.report.FileItem.FormatConfig

final case class FileItem(file: Path, fileType: FileType) extends ReportItem {

  lazy val size: Long = file.toFile.length()

  def format(config: FormatConfig): String = {
    val sizeFormat = if (config.includeSizes) {
      s" [size = $size]"
    } else {
      ""
    }
    s"$file$sizeFormat"
  }
}

object FileItem {

  final case class FormatConfig(includeSizes: Boolean)

  object FormatConfig {

    def apply(): FormatConfig = FormatConfig(includeSizes = false)
  }

  sealed trait SortOrder {

    def ordering: Option[Ordering[FileItem]]
  }

  final case class AlphabeticalOrder(ascending: Boolean) extends SortOrder {

    private implicit val namesOrdering: Ordering[Iterator[String]] = new Ordering[Iterator[String]] {
      override def compare(x: Iterator[String], y: Iterator[String]): Int = {
        (x.hasNext, y.hasNext) match {
          case (true, true) =>
            val z = x.next().compare(y.next())
            if (z == 0) {
              compare(x, y)
            } else {
              z
            }
          case (true, false) => -1
          case (false, true) => 1
          case (false, false) => 0
        }
      }
    }

    override lazy val ordering: Option[Ordering[FileItem]] = {
      reverseDescending(ascending, Ordering.by(_.file.iterator.asScala.map(_.toString.toUpperCase)))
    }
  }

  final case class SizeOrder(ascending: Boolean) extends SortOrder {

    override lazy val ordering: Option[Ordering[FileItem]] = {
      reverseDescending(ascending, Ordering.by(_.size))
    }
  }

  final case object UndefinedOrder extends SortOrder {

    override lazy val ordering: Option[Ordering[FileItem]] = None
  }

  private def reverseDescending(ascending: Boolean, ordering: Ordering[FileItem]) = {
    if (ascending) {
      Some(ordering)
    } else {
      Some(ordering.reverse)
    }
  }
}

