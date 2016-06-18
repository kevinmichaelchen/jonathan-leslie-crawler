package kayhan

/**
  * @author Kevin Chen
  */
case class Date(day: String, month: String, year: String)
object KayhanDateParser {

  def parseKayhanDate(dateString: String): Date = {

    // TODO we're screwed if the date doesn't colon or hyphen
    // dates will be in the form: تاریخ انتشار: ۱۹ خرداد ۱۳۹۵ - ۲۱:۲۷
    // Persian is RTL language, so we go the other way
    val goodStuff = dateString.substring(dateString.indexOf(":") + 1, dateString.indexOf("-")).trim

    val parts = goodStuff.trim.split(" ").map(_.trim)

    Date(parts(0), parts(1), parts(2))
  }

  def main(args: Array[String]): Unit = {
    val s = "تاریخ انتشار: ۱۹ خرداد ۱۳۹۵ - ۲۱:۲۷"
    val d = parseKayhanDate(s)
    println(s"Day ${d.day}")
    println(s"Month ${d.month}")
    println(s"Year ${d.year}")
  }
}
