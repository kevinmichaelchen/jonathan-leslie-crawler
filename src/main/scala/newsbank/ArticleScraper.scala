package newsbank

import java.io.File
import java.sql.Connection

import newsbank.parse._
import util.ExceptionFileLogger

/**
  * @author Kevin Chen
  */
object ArticleScraper {

  def scrapeAndPersistArticle(articleLink: String, cookie: String, connection: Connection, newspaperID: Int, errorLog: File): Boolean = {
    try {
      val article = scrapeArticle(articleLink, cookie)

      val author: Author = article.author

      val byline = if (author.byline.isDefined) s"'${author.byline.get}'" else "NULL"

      val statement = connection.createStatement
      val rs = statement.executeUpdate(
        s"""
           |INSERT INTO `article` (`title`, `articleText`, `section`, `author`, `byline`, `url`, `publishedDate`, `newspaper_id`)
           |VALUES
           |	('${article.title}', '${article.text}', '${article.section}', '${author.name}', ${byline}, '${article.url}', '${article.date}', ${newspaperID});
      """.stripMargin
      )
    } catch {
      case e: Exception => {
        ExceptionFileLogger.log(e, errorLog)
        return false
      }
    }
    true
  }

  def scrapeArticle(articleLink: String, cookie: String): Article = {
    val doc = HttpGetter.get(articleLink, cookie)
    val docHtml = doc.select("div.nb-doc")
    if (docHtml.isEmpty) {
      throw new IllegalStateException(s"div.nb-doc not found on article: ${articleLink}")
    }

    val articleText = ArticleTextParser.parseText(docHtml)

    val title = ArticleTitleParser.parseTitle(docHtml)

    val sourceDateTuple = ArticleSourceAndDateParser.parseSourceAndDate(docHtml)
    val source = sourceDateTuple._1
    val date = sourceDateTuple._2

    val author: Author = ArticleAuthorParser.parseAuthor(docHtml)

    val section = ArticleSectionParser.parseSection(docHtml)

    println(s"Link: ${articleLink}")
    println(s"Title: ${title}")
    println(s"Date: ${date}")
    println(s"Author: ${author}")
    println("")

    // Assume all article URLs start with http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/doc/nb/news/
    val articleSlug = articleLink.substring(articleLink.lastIndexOf("/") + 1)
    Article(articleText, source, title, date, author, section, articleSlug)
  }
}
