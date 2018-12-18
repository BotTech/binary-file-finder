package nz.co.bottech.bff

final case class FileType(category: FileCategory, name: String, details: Option[String]) {

  override def toString: String = {
    val detailsString = details.map(x => s", $x").getOrElse("")
    s"$category, $name$detailsString"
  }
}

object FileType {

  implicit val ordering: Ordering[FileType] = Ordering.by { typ: FileType =>
    FileType.unapply(typ)
  }
}
