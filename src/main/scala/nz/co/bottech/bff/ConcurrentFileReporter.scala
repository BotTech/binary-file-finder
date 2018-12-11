package nz.co.bottech.bff

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

class ConcurrentFileReporter(classifier: FileTypeClassifier)
                            (implicit executionContext: ExecutionContext) extends FileReporter {

  private val files = new ConcurrentHashMap[Path, FileType]()
  private val remaining = new AtomicInteger(0)
  @volatile
  private var awaitingReport = false
  private val reportPromise = Promise[FileReport]()

  override def record(file: Path): Unit = {
    remaining.incrementAndGet()
    val futureType = Future.fromTry(classifier.fileType(file))
    futureType.onComplete {
      case Success(fileType) =>
        files.put(file, fileType)
        if (remaining.decrementAndGet() == 0 && awaitingReport) {
          buildReport()
        }
      case Failure(error) => failReport(error)
    }
  }

  override def report: Future[FileReport] = {
    if (!awaitingReport) {
      synchronized {
        if (!awaitingReport) {
          if (remaining.get() == 0) {
            buildReport()
          } else {
            awaitingReport = true
          }
        }
      }
    }
    reportPromise.future
  }

  private def buildReport(): Unit = {
    reportPromise.success(FileReport(files.asScala.toMap))
  }

  private def failReport(error: Throwable): Unit = {
    reportPromise.failure(error)
  }
}
