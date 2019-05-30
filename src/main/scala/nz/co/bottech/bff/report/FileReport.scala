package nz.co.bottech.bff.report

import java.util.regex.Pattern

import nz.co.bottech.bff.report.FileReport.ReportConfig

class FileReport(files: Seq[FileItem], config: ReportConfig) extends Report[FileItem] {

  def print(): Unit = {
    val filtered = Report.filterItems(files, config.typeFilters)(_.fileType.toString)
    val items = Report.sortItems(filtered, config.fileOrder.ordering)
    items.foreach(printFile)
  }

  private def printFile(file: FileItem): Unit = {
    println(file.format(config.fileFormatConfig))
  }

  def groupByType(config: TypeReport.ReportConfig): TypeReport = {
    val typeMap = files.groupBy(_.fileType).toSeq.map {
      case (typ, fileItems) => TypeItem(typ, fileItems)
    }
    new TypeReport(typeMap, config)
  }
}

object FileReport {

  final case class ReportConfig(fileFormatConfig: FileItem.FormatConfig,
                                typeFilters: Seq[Pattern],
                                fileOrder: FileItem.SortOrder)
}
