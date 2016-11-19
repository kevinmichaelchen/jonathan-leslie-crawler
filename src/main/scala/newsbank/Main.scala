package newsbank

import java.util.Properties
import scala.collection.JavaConverters._

import newsbank.Links.formatUrl

/**
  * @author Kevin Chen
  */
object Main {

  // TODO enable when ready to scrape multiple pages
  val RECURSE = false
  val BASE_URL = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk"

  var numArticlesScraped = 0

  def getCookie() = {
    val prop = new Properties()
    prop.load(getClass.getResourceAsStream("/secrets.properties"))
    val cookie = prop.getProperty("cookie")
    cookie
  }

  def main(args: Array[String]): Unit = {
    val cookie = getCookie()
    println(cookie)
    scrape(formatUrl(Links.jerusalemPostIranArticles), cookie)
  }

  def scrape(link: String, cookie: String): Unit = {
    val doc = HttpGetter.get(link, cookie)

    // Scrape articles
    val articleLinks = doc.select("a.nb-doc-link").asScala
    for( articleLink <- articleLinks ) {
      val href = articleLink.attr("href")
      if (numArticlesScraped > 20) {
        return
      }
      // TODO try catch
      ArticleScraper.scrapeArticle(BASE_URL + href, cookie)
    }

    // Go to the next page
    val pageLinks = doc.select("li.pager-item > a")
    val nextLinkElement = pageLinks.first()
    if (nextLinkElement == null) {
      println("Done...")
    } else {
      val nextLink = nextLinkElement.attr("href")
      println(nextLink)
      if (RECURSE) scrape(BASE_URL + nextLink, cookie)
    }
  }
}
