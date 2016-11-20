package newsbank

import java.sql.{Connection, DriverManager}
import java.util.Properties

import newsbank.Links.formatUrl

import scala.collection.JavaConverters._

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

  def getJdbcPassword() = {
    val prop = new Properties()
    prop.load(getClass.getResourceAsStream("/secrets.properties"))
    val jdbcPassword = prop.getProperty("jdbcPassword")
    jdbcPassword
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

    // TODO renew connection
    var connection: Connection = createNewsbankJdbcConnection

    // TODO do not hard-code
    var newspaperID = 1

    for (articleLink <- articleLinks) {
      val href = articleLink.attr("href")
      if (numArticlesScraped > 5) {
        connection.close()
        return
      }
      ArticleScraper.scrapeAndPersistArticle(BASE_URL + href, cookie, connection, newspaperID)
      numArticlesScraped += 1
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

  def createNewsbankJdbcConnection = {
    val url = "jdbc:mysql://localhost:3306/newsbank"
    val driver = "com.mysql.jdbc.Driver"
    val username = "root"
    val password = getJdbcPassword()
    Class.forName(driver)
    var connection: Connection = DriverManager.getConnection(url, username, password)
    connection
  }
}
