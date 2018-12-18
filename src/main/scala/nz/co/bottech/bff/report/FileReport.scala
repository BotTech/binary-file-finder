package nz.co.bottech.bff.report

import nz.co.bottech.bff.report.FileReport.ReportConfig

class FileReport(files: Seq[FileItem], config: ReportConfig) extends Report[FileItem] {

  def print(): Unit = {
    val items = Report.maybeSorted(files, config.fileOrder.ordering)
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
                                fileOrder: FileItem.SortOrder)
}
