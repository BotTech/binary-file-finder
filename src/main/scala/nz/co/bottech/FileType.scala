package nz.co.bottech

final case class FileType(category: FileCategory, name: String, details: Option[String])

object FileType {

  implicit val ordering: Ordering[FileType] = Ordering.by { typ: FileType =>
    FileType.unapply(typ)
  }
}
