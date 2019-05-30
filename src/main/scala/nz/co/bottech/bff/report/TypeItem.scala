package nz.co.bottech.bff.report

import nz.co.bottech.bff.FileType
import nz.co.bottech.bff.report.TypeItem.FormatConfig

final case class TypeItem(fileType: FileType, fileItems: Seq[FileItem]) extends ReportItem {

  override def toString: String = format(FormatConfig())

  def format(config: FormatConfig): String = {
    val count = if (config.includeCounts) {
      Seq(s"count = ${fileItems.size}")
    } else {
      Seq.empty[String]
    }
    val size = if (config.includeSizes) {
      Seq(s"size = ${fileItems.map(_.size).sum}")
    } else {
      Seq.empty[String]
    }
    val stats = count ++ size
    val statsFormat = if (stats.nonEmpty) {
      stats.mkString(" [", ", ", "]")
    } else {
      ""
    }
    val suffix = if (config.printFiles) {
      ":"
    } else {
      ""
    }
    s"$fileType$statsFormat$suffix"
  }
}

object TypeItem {

  final case class FormatConfig(includeCounts: Boolean,
                                includeSizes: Boolean,
                                printFiles: Boolean,
                                includeFileSizes: Boolean) {

    def fileFormatConfig: FileItem.FormatConfig = {
      FileItem.FormatConfig(includeSizes = includeFileSizes, hideTypes = true)
    }
  }

  object FormatConfig {

    def apply(): FormatConfig = FormatConfig(
      includeCounts = false,
      includeSizes = false,
      printFiles = false,
      includeFileSizes = false
    )
  }

  sealed trait SortOrder {

    def ordering: Option[Ordering[TypeItem]]
  }

  final case class AlphabeticalOrder(ascending: Boolean) extends SortOrder {

    override lazy val ordering: Option[Ordering[TypeItem]] = {
      reverseDescending(ascending, Ordering.by(_.fileType.toString.toUpperCase))
    }
  }

  final case class CountOrder(ascending: Boolean) extends SortOrder {

    override lazy val ordering: Option[Ordering[TypeItem]] = {
      reverseDescending(ascending, Ordering.by(_.fileItems.size))
    }
  }

  final case class SizeOrder(ascending: Boolean) extends SortOrder {

    override lazy val ordering: Option[Ordering[TypeItem]] = {
      reverseDescending(ascending, Ordering.by(_.fileItems.map(_.size).sum))
    }
  }

  final case object UndefinedOrder extends SortOrder {

    override lazy val ordering: Option[Ordering[TypeItem]] = None
  }

  private def reverseDescending(ascending: Boolean, ordering: Ordering[TypeItem]) = {
    if (ascending) {
      Some(ordering)
    } else {
      Some(ordering.reverse)
    }
  }
}

