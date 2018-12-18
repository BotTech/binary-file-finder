package nz.co.bottech.bff.report

import nz.co.bottech.bff.report.TypeReport._

class TypeReport(types: Seq[TypeItem], config: ReportConfig) extends Report[TypeItem] {

  override def print(): Unit = {
    val items = Report.maybeSorted(types, config.typeOrder.ordering)
    items.foreach(printItem)
  }

  private def printItem(item: TypeItem): Unit = {
    println(item.format(config.typeFormatConfig))
    if (config.typeFormatConfig.printFiles) {
      printFiles(item.fileItems)
    }
  }

  private def printFiles(files: Seq[FileItem]): Unit = {
    val items = Report.maybeSorted(files, config.fileOrder.ordering)
    items.foreach(printFile)
  }

  private def printFile(file: FileItem): Unit = {
    println(s"\t${file.format(config.typeFormatConfig.fileFormatConfig)}")
  }
}

object TypeReport {

  final case class ReportConfig(typeFormatConfig: TypeItem.FormatConfig,
                                typeOrder: TypeItem.SortOrder,
                                fileOrder: FileItem.SortOrder)

}
