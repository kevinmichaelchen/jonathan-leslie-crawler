package newsbank

import java.io.File
import java.nio.charset.StandardCharsets
import java.sql.{Connection, DriverManager}
import java.util.Properties

import newsbank.Links.formatUrl
import org.apache.commons.io.FileUtils

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

    // TODO do not hard-code
    val newspaperID = 1
    val link = Links.jerusalemPostIranArticles
    val errorLog = new File("jerusalemPostIranArticles.log")

    FileUtils.deleteQuietly(errorLog)
    FileUtils.write(errorLog, "test\n", StandardCharsets.UTF_8, true)

    scrape(formatUrl(link), cookie, newspaperID, errorLog)
  }

  def scrape(link: String, cookie: String, newspaperID: Int, errorLog: File): Unit = {
    val doc = HttpGetter.get(link, cookie)

    // Scrape articles
    val articleLinks = doc.select("a.nb-doc-link").asScala

    // TODO renew connection
    var connection: Connection = createNewsbankJdbcConnection

    for (articleLink <- articleLinks) {
      val href = articleLink.attr("href")
      if (numArticlesScraped % 5 == 0) {
        // No need to commit since JDBC connection is in auto-commit mode by default
        connection.close()
        connection = createNewsbankJdbcConnection
      }
      if (numArticlesScraped > 15) {
        FileUtils.write(errorLog, "closing connection\n", StandardCharsets.UTF_8, true)
        // No need to commit since JDBC connection is in auto-commit mode by default
        connection.close()
        return
      }
      val success = ArticleScraper.tryScrapeAndPersistArticle(BASE_URL + href, cookie, connection, newspaperID, errorLog)
      if (success) {
        numArticlesScraped += 1
      }
    }

    // Go to the next page
    val pageLinks = doc.select("li.pager-item > a")
    val nextLinkElement = pageLinks.first()
    if (nextLinkElement == null) {
      println("Done...")
    } else {
      val nextLink = nextLinkElement.attr("href")
      println(nextLink)
      if (RECURSE) scrape(BASE_URL + nextLink, cookie, newspaperID, errorLog)
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
