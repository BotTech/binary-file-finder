package nz.co.bottech.bff.report

trait Report[A] {

  def print(): Unit
}

object Report {

  def maybeSorted[A](items: Seq[A], maybeOrdering: Option[Ordering[A]]): Seq[A] = {
    maybeOrdering.map(items.sorted(_)).getOrElse(items)
  }
}
