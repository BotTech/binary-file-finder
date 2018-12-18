package nz.co.bottech.bff.report

import java.nio.file.Path
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

import nz.co.bottech.bff.FileTypeClassifier

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

class ConcurrentFileReporter(classifier: FileTypeClassifier, config: FileReport.ReportConfig)
                            (implicit executionContext: ExecutionContext) extends FileReporter {

  private val files = new ConcurrentLinkedQueue[FileItem]()
  private val remaining = new AtomicInteger(0)
  @volatile
  private var awaitingReport = false
  private val reportPromise = Promise[FileReport]()

  override def record(file: Path): Boolean = {
    if (reportPromise.isCompleted) {
      false
    } else {
      remaining.incrementAndGet()
      val futureType = Future.fromTry(classifier.fileType(file))
      futureType.onComplete {
        case Success(fileType) =>
          files.add(FileItem(file, fileType))
          if (remaining.decrementAndGet() == 0 && awaitingReport) {
            buildReport()
          }
        case Failure(error) => failReport(error)
      }
      true
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
    reportPromise.success(new FileReport(files.asScala.toSeq, config))
  }

  private def failReport(error: Throwable): Unit = {
    reportPromise.tryFailure(error)
  }
}
