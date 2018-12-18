package nz.co.bottech.bff

import java.nio.file.{Files, Path}
import java.util.regex.Pattern

import nz.co.bottech.bff.report.ConcurrentFileReporter

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object BinaryFileFinder extends App {

  ArgParser().parse(args, Config()).foreach(run)

  private def run(config: Config): Unit = {
    val classifier = BSDFileTypeClassifier
    val reporter = new ConcurrentFileReporter(classifier, config.fileReportConfig)
    val visitor = new FileTypeVisitor(excludeMatching(config), reporter)
    Files.walkFileTree(config.dir, visitor)
    val fileReport = Await.result(reporter.report, Duration.Inf)
    val report = if (config.groupByTypes) {
      fileReport.groupByType(config.typeReportConfig)
    } else {
      fileReport
    }
    report.print()
  }

  private def excludeMatching(config: Config) = {
    val predicates = patternPredicates(config.excludePaths, identity) ++
      patternPredicates(config.excludeNames, _.getFileName)
    file: Path => predicates.exists(_.apply(file))
  }

  private def patternPredicates(patterns: Seq[Pattern], f: Path => Path) = {
    patterns.map { pattern =>
      file: Path => completeMatch(pattern, f(file))
    }
  }

  private def completeMatch(pattern: Pattern, path: Path) = {
    val matcher = pattern.matcher(path.toString)
    matcher.find() && matcher.start() == 0 && matcher.hitEnd()
  }
}
