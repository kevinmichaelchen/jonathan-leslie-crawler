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
    println(s"printing ${newsLinkElements.size} links")

    val newsLinks = newsLinkElements.map(baseUrl + _.attr("href"))

//    newsLinks.foreach(scrape)
    scrape(newsLinks.head)
  }

  def scrape(url: String): Unit = {
    println(s"Scraping ${url}")
    val doc = Jsoup.connect(url).get()
    val date = doc.select("div[class='news_nav news_pdate_c']")

    // TODO    println(date.text())

    val d = DateScraper.parseKayhanDate(date.text())
    println(s"Day ${d.day}")
    println(s"Month ${d.month}")
    println(s"Year ${d.year}")

//    val dateTerms = parseDate(date.first().text())
//    println(s"Day ${dateTerms(0)} Month ${dateTerms(1)} Year ${dateTerms(2)}")
  }
}