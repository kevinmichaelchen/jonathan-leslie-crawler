import org.jsoup.Jsoup

/**
  * @author Kevin Chen
  */
class Crawler {

}

object Crawler {
  def main(args: Array[String]): Unit = {
    val url = "http://kayhan.ir/fa/search"
    println(s"Hitting ${url}")
    var doc = Jsoup.connect(url)
      .data("query", "اسرائیل")
      .post()
    println("Got text:")
    println(doc.text())
  }
}