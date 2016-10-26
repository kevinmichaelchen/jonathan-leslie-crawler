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
      // TODO try catch
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

    val articleTextElement = docHtml.select("div.body")
    val articleText = articleTextElement.text()

    val titleElement = docHtml.select("div.title h2")
    val title = titleElement.text()

    val sourceElement = docHtml.select("div.source")
    val sourceText = sourceElement.text()
    val split = sourceText.split("-")
    val source = split(0).trim
    val dateRegex = "(.*\\d\\d\\d\\d)(.*)".r
    val date = dateRegex.findFirstMatchIn(split(1).trim).get.group(1)

    val moreDetailsElement = docHtml.select("div.moredetails")
    val authorBylineElement = moreDetailsElement.select("li.author").select("span.val")
    val authorBylineText = authorBylineElement.text()
    val authorBylineSplit = authorBylineText.split("/")
    val author = authorBylineSplit(0)
    val byline = authorBylineSplit(1)

    numArticlesScraped += 1
    println(s"Text: ${articleText}")
    println(s"Source: ${source}")
    println(s"Title: ${title}")
    println(s"Date: ${date}")
    println(s"Author: ${author}")
    println(s"Byline: ${byline}")
    println(articleLink)
  }

  def get(url: String, cookie: String): Document =
    Jsoup.connect(url)
      .timeout(6000)
      .cookie("ezproxy", cookie)
      // TODO fix timeout error
      .get
}
