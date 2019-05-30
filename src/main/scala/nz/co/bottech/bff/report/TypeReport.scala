package nz.co.bottech.bff.report

import java.util.regex.Pattern

import nz.co.bottech.bff.report.TypeReport._

class TypeReport(types: Seq[TypeItem], config: ReportConfig) extends Report[TypeItem] {

  override def print(): Unit = {
    val filtered = Report.filterItems(types, config.typeFilters)(_.fileType.toString)
    val items = Report.sortItems(filtered, config.typeOrder.ordering)
    items.foreach(printItem)
  }

  private def printItem(item: TypeItem): Unit = {
    println(item.format(config.typeFormatConfig))
    if (config.typeFormatConfig.printFiles) {
      printFiles(item.fileItems)
    }
  }

  private def printFiles(files: Seq[FileItem]): Unit = {
    val items = Report.sortItems(files, config.fileOrder.ordering)
    items.foreach(printFile)
  }

  private def printFile(file: FileItem): Unit = {
    println(s"\t${file.format(config.typeFormatConfig.fileFormatConfig)}")
  }
}

object TypeReport {

  final case class ReportConfig(typeFormatConfig: TypeItem.FormatConfig,
                                typeFilters: Seq[Pattern],
                                typeOrder: TypeItem.SortOrder,
                                fileOrder: FileItem.SortOrder)

}
