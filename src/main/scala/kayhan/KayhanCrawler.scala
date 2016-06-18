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

  // debug variables
  val ARTICLES_TO_SCRAPE_PER_PAGE = 1
  val TOTAL_NUMBER_ARTICLES_TO_SCRAPE_BEFORE_ABORTING = 3
  var numScrapedPages = 0
  var numScrapedArticles = 0

  def main(args: Array[String]): Unit = {
    val searchTerms: Map[String, String] = Map(
      "israel" -> "اسرائیل",
      "zionist" -> "صهیونیستی"
    )

    searchTerms foreach {
      case (k, v) => startCrawl(k, v, 1)
    }
  }

  def startCrawl(englishSearchTerm: String, searchTerm: String, pageNumber: Int): Unit = {

    val url = s"${BASE_URL}/fa/search"
    println(s"Hitting ${url}")

    val doc = Jsoup.connect(url)
      .data("query", searchTerm)
      .data("rpp", "50")
      .post()

    scrapeArticlesOnPage(doc)
    crawlNextPage(doc, englishSearchTerm, searchTerm, pageNumber)
  }

  def crawlNextPage(doc: Document, englishSearchTerm: String, searchTerm: String, pageNumber: Int): Unit = {
    if (numScrapedArticles > TOTAL_NUMBER_ARTICLES_TO_SCRAPE_BEFORE_ABORTING) return

    println(s"Crawling page ${pageNumber} of results for '${englishSearchTerm}'")
    // TODO delete this when we're all done
    val nextPageRelativeUrl = getNextPageHref(doc)
    if (nextPageRelativeUrl.isDefined) {
      numScrapedPages += 1
      crawlRecursive(englishSearchTerm, searchTerm, nextPageRelativeUrl.get, pageNumber + 1)
    } else {
      println("Done crawling...")
    }
  }

  def crawlRecursive(englishSearchTerm: String, searchTerm: String, pageUrl: String, currentPageNumber: Int): Unit = {
    // Get doc
    // http://kayhan.ir/fa/search/2/-1/-1/50/اسرائیل?from=1392/07/06&to=1395/03/30
    val url = BASE_URL + pageUrl

    println(s"Getting ${url}")
    val doc = Jsoup.connect(url).post()
    scrapeArticlesOnPage(doc)
    crawlNextPage(doc, englishSearchTerm, searchTerm, currentPageNumber)
  }

  def scrapeArticlesOnPage(doc: Document): Unit = {
    // Get links for scraping
    val linkElements: Elements = doc.select("a[href]")
    val newsLinkElements = linkElements.toSet
      .filter(_.attr("href").contains("/fa/news/"))
      .toList
    println(s"Scraping ${newsLinkElements.size} links")
    val newsLinks = newsLinkElements.map(BASE_URL + _.attr("href"))

    // TODO put back when we're ready
    // Scrape all news articles on this page
    //    newsLinks.foreach(scrape)
    // Scrape first couple
    newsLinks.slice(0, ARTICLES_TO_SCRAPE_PER_PAGE).foreach(scrape)
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
    //println(s"    BODY: ${body}")

    println("")

    numScrapedArticles += 1
  }
}