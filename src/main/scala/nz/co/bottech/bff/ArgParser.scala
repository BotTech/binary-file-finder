package nz.co.bottech.bff

import java.io.File
import java.util.regex.Pattern

import scopt.OptionParser

class ArgParser extends OptionParser[Config]("bff") {
  head("Binary File Finder", "version 1.0")
  opt[String]('e', "exclude-name")
    .text("Zero or more Java regular expressions which will exclude directories and files whose name matches.")
    .valueName("regex")
    .optional()
    .unbounded()
    .action {
      case (x, c) =>
        val exclude = Pattern.compile(x)
        c.copy(excludeNames = c.excludeNames :+ exclude)
    }
  opt[String]('x', "exclude-path")
    .text("Zero or more Java regular expressions which will exclude directories and files whose relative path matches.")
    .valueName("regex")
    .optional()
    .unbounded()
    .action {
      case (x, c) =>
        val exclude = Pattern.compile(x)
        c.copy(excludePaths = c.excludePaths :+ exclude)
    }
  help("help")
    .text("prints this usage text.")
  arg[File]("dir")
    .text("The directory to start searching from.")
    .required()
    .action {
      case (x, c) => c.copy(dir = x.toPath)
    }
}

object ArgParser {

  def apply() = new ArgParser
}
