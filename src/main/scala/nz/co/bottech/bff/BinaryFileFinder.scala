package nz.co.bottech.bff

import java.nio.file.{Files, Path}
import java.util.regex.Pattern

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object BinaryFileFinder extends App {

  ArgParser().parse(args, Config()).foreach(run)

  private def run(config: Config): Unit = {
    val classifier = BSDFileTypeClassifier
    val reporter = new ConcurrentFileReporter(classifier)
    val visitor = new FileTypeVisitor(excludeMatching(config), reporter)
    Files.walkFileTree(config.dir, visitor)
    val report = Await.result(reporter.report, Duration.Inf)
    report.groupByType.sort.printTypes()
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
