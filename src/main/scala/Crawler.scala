import org.jsoup.Jsoup
import org.jsoup.select.Elements

import scala.collection.JavaConversions._

/**
  * @author Kevin Chen
  */
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

    val linkElements: Elements = doc.select("a[href]")

//    links.foreach(println)

    val newsLinkElements = linkElements.toSet
      .filter(_.attr("href").contains("/fa/news/"))
      .toList
    println(s"Scraping ${newsLinkElements.size} links")

    val newsLinks = newsLinkElements.map(baseUrl + _.attr("href"))

//    newsLinks.foreach(scrape)
    // Scrape first couple
    newsLinks.slice(0, 5).foreach(scrape)
  }

  def scrape(url: String): Unit = {
    println(s"Scraping ${url}")
    val doc = Jsoup.connect(url).get()

    val date = doc.select("div[class='news_nav news_pdate_c']").text()

    val d = DateScraper.parseKayhanDate(date)
    println(s"     DAY: ${d.day}")
    println(s"   MONTH: ${d.month}")
    println(s"    YEAR: ${d.year}")

    val title = doc.select("div[class='title']").text()
    println(s"   TITLE: ${title}")

    var subtitle = doc.select("div[class='subtitle']").text()
    subtitle = if (subtitle.trim.isEmpty) "EMPTY" else subtitle
    println(s"SUBTITLE: ${subtitle}")

    println("")
  }
}