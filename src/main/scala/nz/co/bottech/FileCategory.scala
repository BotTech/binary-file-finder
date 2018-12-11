package nz.co.bottech

sealed trait FileCategory

object FileCategory {

  implicit val ordering: Ordering[FileCategory] = Ordering.by {
    case Text => 1
    case Binary => 2
    case Unknown => 3
  }
}

case object Binary extends FileCategory

case object Text extends FileCategory

case object Unknown extends FileCategory
