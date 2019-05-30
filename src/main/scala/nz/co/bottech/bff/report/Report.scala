package nz.co.bottech.bff.report

import java.util.regex.Pattern

trait Report[A] {

  def print(): Unit
}

object Report {

  def filterItems[A](items: Seq[A], filters: Seq[Pattern])(f: A => String): Seq[A] = {
    items.filterNot { item =>
      filters.exists { filter =>
        filter.matcher(f(item)).find()
      }
    }
  }

  def sortItems[A](items: Seq[A], maybeOrdering: Option[Ordering[A]]): Seq[A] = {
    maybeOrdering.map(items.sorted(_)).getOrElse(items)
  }
}
