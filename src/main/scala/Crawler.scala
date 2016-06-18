import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.JavaConversions._

/**
  * @author Kevin Chen
  */
object Crawler {
  def main(args: Array[String]): Unit = {
    val israelSearchTerm = "اسرائیل"
    val zionistSearchTerm = "صهیونیستی"

    // http://kayhan.ir/fa/search/2/-1/-1/50/%D8%A7%D8%B3%D8%B1%D8%A7%D8%A6%DB%8C%D9%84?from=1392/07/06&to=1395/03/30
    // http://kayhan.ir/fa/search/2/-1/-1/50/اسرائیل?from=1392/07/06&to=1395/03/30

    val baseUrl = "http://kayhan.ir"
    val url = s"${baseUrl}/fa/search"
    println(s"Hitting ${url}")

    val doc = Jsoup.connect(url)
      .data("query", israelSearchTerm)
      .data("rpp", "50")
      .post()

    // Get links for scraping
    val linkElements: Elements = doc.select("a[href]")
    val newsLinkElements = linkElements.toSet
      .filter(_.attr("href").contains("/fa/news/"))
      .toList
    println(s"Scraping ${newsLinkElements.size} links")
    val newsLinks = newsLinkElements.map(baseUrl + _.attr("href"))

    // Scrape all news articles on this page
//    newsLinks.foreach(scrape)
    // Scrape first couple
//    newsLinks.slice(0, 2).foreach(scrape)

    // Go to the next page
    val nextPageRelativeUrl = getNextPageHref(doc)
  }

  def getNextPageHref(doc: Document): Option[String] = {
    val paginationElements = doc.select("div[class='pagination'] > *")
    println(s"Found ${paginationElements.size()} pagination elements")
    paginationElements.foreach(println)
    val spanIndex = paginationElements.indexWhere(_.tagName.equals("span"))
    if (spanIndex != -1) {
      val nextPage = paginationElements.drop(spanIndex).filter(_.tagName.equals("a")).head
      println(s"Next page: ${nextPage}")
      Some(nextPage.attr("href"))
    } else {
      None
    }
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

    val body = doc.select("div[class='body']").text()
    println(s"    BODY: ${body}")

    println("")
  }
}