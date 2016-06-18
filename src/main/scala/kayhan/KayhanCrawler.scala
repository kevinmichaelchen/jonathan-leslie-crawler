package kayhan

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.JavaConversions._

/**
  * @author Kevin Chen
  */
object KayhanCrawler {
  val BASE_URL = "http://kayhan.ir"

  def main(args: Array[String]): Unit = {
    val searchTerms: Map[String, String] = Map(
      "israel" -> "اسرائیل",
      "zionist" -> "صهیونیستی"
    )

    searchTerms foreach {
      case (k,v) => crawl(k, v)
    }
  }

  def crawl(englishSearchTerm: String, searchTerm: String): Unit = {

    val url = s"${BASE_URL}/fa/search"
    println(s"Hitting ${url}")

    val doc = Jsoup.connect(url)
      .data("query", searchTerm)
      .data("rpp", "50")
      .post()

    // Get links for scraping
    val linkElements: Elements = doc.select("a[href]")
    val newsLinkElements = linkElements.toSet
      .filter(_.attr("href").contains("/fa/news/"))
      .toList
    println(s"Scraping ${newsLinkElements.size} links")
    val newsLinks = newsLinkElements.map(BASE_URL + _.attr("href"))

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

    val d = KayhanDateParser.parseKayhanDate(date)
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