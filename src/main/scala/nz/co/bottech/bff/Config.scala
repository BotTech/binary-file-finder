package nz.co.bottech.bff

import java.nio.file.{Path, Paths}
import java.util.regex.Pattern

import nz.co.bottech.bff.report.{FileItem, FileReport, TypeItem, TypeReport}

final case class Config(dir: Path = Paths.get(""),
                        excludeNames: Seq[Pattern] = Vector.empty,
                        excludePaths: Seq[Pattern] = Vector.empty,
                        typeFilters: Seq[Pattern] = Vector.empty,
                        fileFormatConfig: FileItem.FormatConfig = FileItem.FormatConfig(),
                        fileOrder: FileItem.SortOrder = FileItem.UndefinedOrder,
                        groupByTypes: Boolean = false,
                        typeFormatConfig: TypeItem.FormatConfig = TypeItem.FormatConfig(),
                        typeOrder: TypeItem.SortOrder = TypeItem.UndefinedOrder) {

  def fileReportConfig: FileReport.ReportConfig = {
    FileReport.ReportConfig(fileFormatConfig, typeFilters, fileOrder)
  }

  def typeReportConfig: TypeReport.ReportConfig = {
    TypeReport.ReportConfig(typeFormatConfig, typeFilters, typeOrder, fileOrder)
  }
}

object Config {

  def apply(): Config = Config(
    Paths.get(""),
    Vector.empty,
    Vector.empty,
    Vector.empty,
    FileItem.FormatConfig(),
    FileItem.UndefinedOrder,
    groupByTypes = false,
    TypeItem.FormatConfig(),
    TypeItem.UndefinedOrder
  )
}
