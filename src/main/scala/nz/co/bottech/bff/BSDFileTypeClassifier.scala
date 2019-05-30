package nz.co.bottech.bff

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.immutable.SortedSet
import scala.sys.process._
import scala.util.Try

object BSDFileTypeClassifier extends FileTypeClassifier {

  private final val FirstLineRegex = "(.*)".r
  private final val LineDetailsRegex = "(.*), ?(.*)".r

  private final val BinaryPatternsHeader = "Binary patterns:"
  private final val TextPatternsHeader = "Text patterns:"
  private final val PatternRegex = """Strength = +(\d+)@(\d+): (.*) \[(.*)]""".r

  // TODO: Use a memory sensitive cache.
  private val lineCache = new ConcurrentHashMap[String, FileType]().asScala

  private val typeMatcher = Try {
    val lines = Seq("file", "--list").lineStream
    new FileTypeMatcher(lines)
  }

  override def fileType(file: Path): Try[FileType] = {
    Try {
      // This sometimes returns multiple lines when the binary supports two architectures
      // but the first line is enough. The subsequent lines contain the file name even though
      // we asked for the brief output.
      val result = Seq("file", "--brief", file.toString).!!
      val line = FirstLineRegex.findFirstIn(result).get
      lineCache.getOrElseUpdate(line, matchLine(line))
    }
  }

  private def matchLine(line: String): FileType = {
    val (name, details) = line match {
      case LineDetailsRegex(typeName, typeDetails) => typeName -> Some(typeDetails)
      case typeName => typeName -> None
    }
    typeMatcher.get.matchName(name, details)
  }

  private class FileTypeMatcher(patterns: Seq[String]) {

    private final case class FilePattern(category: FileCategory,
                                         strength: Int,
                                         offset: Long,
                                         typeFormat: String,
                                         mimeType: Option[String]) {

      private val nameParts: Seq[String] = {
        val parts = typeFormat.split('%')
        parts.map {
          case "" => "%"
          case part =>
            val conversionPos = part.indexWhere(_.isLetter)
            part.drop(conversionPos + 1)
        }
      }

      def matches(value: String): Boolean = {
        @tailrec
        def loop(remainingParts: Seq[String], remainingValue: String): Boolean = {
          remainingParts match {
            case Seq() => true
            case head +: tail =>
              val i = remainingValue.indexOf(head)
              if (i >= 0) {
                loop(tail, remainingValue.drop(i + head.length))
              } else {
                false
              }
          }
        }

        loop(nameParts, value)
      }
    }

    private implicit val filePatternOrdering: Ordering[FilePattern] = Ordering.by {
      pattern: FilePattern => (pattern.category, -pattern.typeFormat.length, pattern.typeFormat)
    }

    private val filePatterns = {
      @tailrec
      def loop(remaining: Seq[String], category: FileCategory, acc: Set[FilePattern]): Set[FilePattern] = {
        remaining match {
          case Seq() => acc.filter(_.typeFormat.trim.nonEmpty)
          case head +: tail => head match {
            case BinaryPatternsHeader =>
              loop(tail, Binary, acc)
            case TextPatternsHeader =>
              loop(tail, Text, acc)
            case PatternRegex(strength, offset, typeFormat, mimeType) =>
              val maybeMimeType = Some(mimeType).filter(_.nonEmpty)
              val boundedStrength = Try(strength.toInt).getOrElse(Int.MaxValue)
              val pattern = FilePattern(category, boundedStrength, offset.toLong, typeFormat, maybeMimeType)
              loop(tail, category, acc + pattern)
            case _ => loop(tail, Unknown, acc)
          }
        }
      }

      loop(patterns, Unknown, SortedSet.empty)
    }

    def matchName(name: String, details: Option[String]): FileType = {
      val pattern = filePatterns.find(_.matches(name))
      val category = pattern.map(_.category).getOrElse(Unknown)
      FileType(category, name, details)
    }
  }

}
