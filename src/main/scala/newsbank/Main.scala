package newsbank

import java.util.Properties
import scala.collection.JavaConverters._

import newsbank.Links.formatUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
  * @author Kevin Chen
  */
class Main {
}

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
    val doc = get(link, cookie)

    val html = doc.html()

    // Scrape articles
    val articleLinks = doc.select("a.nb-doc-link").asScala
    for( articleLink <- articleLinks ) {
      val href = articleLink.attr("href")
      if (numArticlesScraped != 0) {
        return
      }
      scrapeArticle(BASE_URL + href, cookie)
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

  def scrapeArticle(articleLink: String, cookie: String): Unit = {
    val doc = get(articleLink, cookie)
    val docHtml = doc.select("div.nb-doc")

    val title = docHtml.select("div.title h2")

    val source = docHtml.select("div.source")
    println(doc)
    numArticlesScraped += 1
    println(articleLink)
  }

  def get(url: String, cookie: String): Document =
    Jsoup.connect(url)
      .timeout(6000)
      .cookie("ezproxy", cookie)
      // TODO fix timeout error
      .get
}
