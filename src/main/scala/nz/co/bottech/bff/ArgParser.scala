package nz.co.bottech.bff

import java.io.File
import java.util.regex.Pattern

import nz.co.bottech.bff.report.{FileItem, TypeItem}
import scopt.OptionParser

class ArgParser extends OptionParser[Config]("bff") {

  head("Binary File Finder", "version 1.0")

  note("Finds all files within a directory and determines what type of file they are.")

  opt[String]("exclude-name")
    .abbr("e")
    .valueName("<regex>")
    .text("Pattern to exclude directories and files whose name matches. This option may be specified multiple times.")
    .optional()
    .unbounded()
    .action {
      case (x, c) =>
        val exclude = Pattern.compile(x)
        c.copy(excludeNames = c.excludeNames :+ exclude)
    }

  opt[String]("filter-types")
    .abbr("f")
    .valueName("<regex>")
    .text("Pattern to filter out any results that match. This option may be specified multiple times.")
    .optional()
    .unbounded()
    .action {
      case (x, c) =>
        val filter = Pattern.compile(x)
        c.copy(typeFilters = c.typeFilters :+ filter)
    }

  private val groupTypeOptions: Map[String, TypeItem.FormatConfig => TypeItem.FormatConfig] = Map(
    "c" -> (_.copy(includeCounts = true)),
    "s" -> (_.copy(includeSizes = true)),
    "f" -> (_.copy(printFiles = true)),
    "fs" -> (_.copy(includeFileSizes = true))
  )

  opt[Seq[String]]("group-types")
    .abbr("g")
    .valueName(groupTypeOptions.keys.mkString(","))
    .text("""Group files into types. Values are; counts ("c"), include sizes ("s"), print files ("f"), and print file sizes ("fs").""")
    .validate { x =>
      val invalid = x.toSet -- groupTypeOptions.keys
      if (invalid.isEmpty) {
        success
      } else {
        failure(s"""Invalid values for --group-types: "${invalid.mkString(",")}".""")
      }
    }
    .action {
      case (x, c) =>
        val formatConfig = x.foldLeft(c.typeFormatConfig) {
          case (config, value) => groupTypeOptions.getOrElse(value, identity[TypeItem.FormatConfig] _)(config)
        }
        c.copy(typeFormatConfig = formatConfig, groupByTypes = true)
    }

  help("help")
    .abbr("h")
    .text("prints this usage message.")

  private val sortFileOptions: Map[String, FileItem.SortOrder] = Map(
    "+a" -> FileItem.AlphabeticalOrder(true),
    "-a" -> FileItem.AlphabeticalOrder(false),
    "+s" -> FileItem.SizeOrder(true),
    "-s" -> FileItem.SizeOrder(false)
  )

  opt[String]("sort-files")
    .abbr("s")
    .valueName("<value>")
    .text("""Sorts the files in the report. Values are; alphabetical ("a") and size ("s").""")
    .optional()
    .maxOccurs(1)
    .validate { x =>
      if (sortFileOptions.contains(x)) {
        success
      } else {
        failure(s"""Invalid value for --sort-files: "$x".""")
      }
    }
    .action {
      case (x, c) =>
        sortFileOptions.get(x) match {
          case Some(order) => c.copy(fileOrder = order)
          case None => c
        }
    }

  private val sortTypeOptions: Map[String, TypeItem.SortOrder] = Map(
    "+a" -> TypeItem.AlphabeticalOrder(true),
    "-a" -> TypeItem.AlphabeticalOrder(false),
    "+c" -> TypeItem.CountOrder(true),
    "-c" -> TypeItem.CountOrder(false),
    "+s" -> TypeItem.SizeOrder(true),
    "-s" -> TypeItem.SizeOrder(false)
  )

  opt[String]("sort-types")
    .abbr("t")
    .valueName("<value>")
    .text("""Sorts the types in the report. Values are; alphabetical ("a"), count ("c") and size ("s"). Only valid if --group-types is also specified.""")
    .optional()
    .maxOccurs(1)
    .validate { x =>
      if (sortTypeOptions.contains(x)) {
        success
      } else {
        failure(s"""Invalid value for --sort-types: "$x".""")
      }
    }
    .action {
      case (x, c) =>
        sortTypeOptions.get(x) match {
          case Some(order) => c.copy(typeOrder = order)
          case None => c
        }
    }

  opt[String]("exclude-path")
    .abbr("x")
    .valueName("<regex>")
    .text("Pattern to exclude directories and files whose relative path matches. This option may be specified multiple times.")
    .optional()
    .unbounded()
    .action {
      case (x, c) =>
        val exclude = Pattern.compile(x)
        c.copy(excludePaths = c.excludePaths :+ exclude)
    }

  arg[File]("<dir>")
    .text("The directory to start searching from.")
    .required()
    .action {
      case (x, c) => c.copy(dir = x.toPath)
    }
}

object ArgParser {

  def apply() = new ArgParser
}
