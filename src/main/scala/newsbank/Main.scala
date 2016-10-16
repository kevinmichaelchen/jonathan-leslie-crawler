package newsbank

import java.util.Properties

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
    val doc = getLink(link, cookie)

    val html = doc.html()

//    println(html)
    val articleLinks = doc.select("a.nb-doc-link")
    println(articleLinks.size())

    val pageLinks = doc.select("li.pager-item > a")
    println(pageLinks.size())

    val nextLinkElement = pageLinks.first()
    if (nextLinkElement == null) {
      println("Done...")
    } else {
      val nextLink = nextLinkElement.attr("href")
      println(nextLink)

      if (RECURSE) scrape(BASE_URL + nextLink, cookie)
    }
  }

  def getLink(link: String, cookie: String): Document =
    Jsoup.connect(link)
      .cookie("ezproxy", cookie)
      // TODO fix timeout error
      .get
}
