package newsbank

import java.io.File
import java.sql.Connection

import newsbank.parse._
import util.{ExceptionFileLogger, SqlStringEscaper}

/**
  * @author Kevin Chen
  */
object ArticleScraper {

  def tryScrapeAndPersistArticle(articleLink: String, cookie: String, connection: Connection, newspaperID: Int, errorLog: File): Boolean = {
    var sql = ""
    try {
      val article = scrapeArticle(articleLink, cookie)
      val author: Author = article.author

      val articleTitle = SqlStringEscaper.escape(article.title)
      val articleText = SqlStringEscaper.escape(article.text)
      val articleSection = SqlStringEscaper.escape(article.section)
      val articleAuthor = SqlStringEscaper.escape(author.name)
      val byline = if (author.byline.isDefined) s"'${SqlStringEscaper.escape(author.byline.get)}'" else "NULL"

      val statement = connection.createStatement
      sql =
        s"""
           |INSERT INTO `article` (`title`, `articleText`, `section`, `author`, `byline`, `url`, `publishedDate`, `newspaper_id`)
           |VALUES
           |	('${articleTitle}', '${articleText}', '${articleSection}', '${articleAuthor}', ${byline}, '${article.url}', '${article.date}', ${newspaperID});
      """.stripMargin
      val rs = statement.executeUpdate(sql)
    } catch {
      case e: Exception => {
        ExceptionFileLogger.log(s"Error with sql: ${sql}", errorLog)
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
