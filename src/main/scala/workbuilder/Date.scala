package workbuilder

case class Date(year: Int, month: Int, day: Int) {}

object Date {
  def apply(raw: String): Date = {
    val p = raw.split("/").map(_.toInt)
    Date(p(0), p(1), p(2))
  }

  def apply(): Date = {
    Date(2014, 6, 1) // Twitter始めたのがこのあたりらしい
  }

  implicit object DateOrdering extends Ordering[Date] {
    override def compare(a: Date, b: Date): Int = {
      if (a.year != b.year) a.year.compare(b.year);
      else if (a.month != b.month) a.month.compare(b.month);
      else a.day.compare(b.day);
    }
  }
  implicit def orderingToOrdered(v: Date): Ordered[Date] = scala.math.Ordered.orderingToOrdered(v)(DateOrdering)
}
