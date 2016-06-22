package kayhan

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.JavaConversions._

/**
  * @author Kevin Chen
  */
case class ScrapeCount(var count: Int, limit: Option[Int])

case class Article(day: String, month: String, year: String,
                   title: String, subtitle: String, body: String)

object KayhanCrawler {
  val BASE_URL = "http://kayhan.ir"

  // debug variables
  val ARTICLES_TO_SCRAPE_PER_PAGE = 1
  var numScrapedPages = 0

  val SCRAPE_COUNTS: Map[String, ScrapeCount] = Map(
    "israel" -> ScrapeCount(0, Some(3)),
    "zionist" -> ScrapeCount(0, Some(3))
  )

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

    scrapeArticlesOnPage(doc, englishSearchTerm)
    crawlNextPage(doc, englishSearchTerm, searchTerm, pageNumber)
  }

  def crawlNextPage(doc: Document, englishSearchTerm: String, searchTerm: String, pageNumber: Int): Unit = {
    val limit = SCRAPE_COUNTS.get(englishSearchTerm).get.limit
    if (limit.isDefined && SCRAPE_COUNTS.get(englishSearchTerm).get.count > limit.get) {
      println(s"You've scraped enough articles. Unable to scrape page ${pageNumber} for ${englishSearchTerm}")
      return
    }

    println(s"Crawling page ${pageNumber} of results for '${englishSearchTerm}'")
    // TODO delete this when we're all done
    val nextPageRelativeUrl = getNextPageHref(doc)
    if (nextPageRelativeUrl.isDefined) {
      numScrapedPages += 1
      crawlRecursive(englishSearchTerm, searchTerm, nextPageRelativeUrl.get, pageNumber + 1)
    } else {
      println("Done crawling...")
      println(s"Scraped ${numScrapedPages} pages.")
      for ((searchTerm, scrapeCount) <- SCRAPE_COUNTS) {
        println(s"Scraped ${scrapeCount.count} pages for '${searchTerm}'")
      }
    }
  }

  def crawlRecursive(englishSearchTerm: String, searchTerm: String, pageUrl: String, currentPageNumber: Int): Unit = {
    // Get doc
    // http://kayhan.ir/fa/search/2/-1/-1/50/اسرائیل?from=1392/07/06&to=1395/03/30
    val url = BASE_URL + pageUrl

    println(s"Getting ${url}")
    val doc = Jsoup.connect(url).post()
    scrapeArticlesOnPage(doc, englishSearchTerm)
    crawlNextPage(doc, englishSearchTerm, searchTerm, currentPageNumber)
  }

  def scrapeArticlesOnPage(doc: Document, englishSearchTerm: String): Unit = {
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
    newsLinks.slice(0, ARTICLES_TO_SCRAPE_PER_PAGE).foreach(scrape(_, englishSearchTerm))
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

  def scrape(url: String, englishSearchTerm: String): Unit = {
    println(s"Scraping ${url}")
    val doc = Jsoup.connect(url).get()

    val dateString = doc.select("div[class='news_nav news_pdate_c']").text()
    val date = KayhanDateParser.parseKayhanDate(dateString)

    val day = date.day
    val month = date.month
    val year = date.year
    val title = doc.select("div[class='title']").text()
    var subtitle = doc.select("div[class='subtitle']").text()
    subtitle = if (subtitle.trim.isEmpty) "EMPTY" else subtitle

    val body = doc.select("div[class='body']").text()

    println(s"     DAY: ${day}")
    println(s"   MONTH: ${month}")
    println(s"    YEAR: ${year}")
    println(s"   TITLE: ${title}")
    println(s"SUBTITLE: ${subtitle}")
    //println(s"    BODY: ${body}")

    MongoPersister.persist(Article(day, month, year, title, subtitle, body), url)

    println(s"Prev count for ${englishSearchTerm}: ${SCRAPE_COUNTS.get(englishSearchTerm).get.count}")
    SCRAPE_COUNTS.get(englishSearchTerm).get.count += 1
    println(s"Curr count for ${englishSearchTerm}: ${SCRAPE_COUNTS.get(englishSearchTerm).get.count}")

    println("")
  }
}