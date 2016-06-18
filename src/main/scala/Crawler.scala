import org.jsoup.Jsoup
import org.jsoup.select.Elements

import scala.collection.JavaConversions._

/**
  * @author Kevin Chen
  */
class Crawler {

}

object Crawler {
  def main(args: Array[String]): Unit = {
    val israelSearchTerm = "اسرائیل"
    val zionistSearchTerm = "صهیونیستی"

    val baseUrl = "http://kayhan.ir"
    val url = s"${baseUrl}/fa/search"
    println(s"Hitting ${url}")
    val doc = Jsoup.connect(url)
      .data("query", israelSearchTerm)
      .data("rpp", "50")
      .post()
    println("Got text:")
    println(doc.text())

    val links: Elements = doc.select("a[href]")
    val newsLinks = links.toSet.filter(_.attr("href").contains("/fa/news/"))
    println(s"printing ${newsLinks.size} links")
    newsLinks.map(_.attr("href")).foreach(println)


  }
}