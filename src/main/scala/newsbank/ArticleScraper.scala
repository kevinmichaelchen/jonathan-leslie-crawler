package newsbank

import newsbank.parse._
import java.sql.{Connection}

/**
  * @author Kevin Chen
  */
object ArticleScraper {

  def scrapeAndPersistArticle(articleLink: String, cookie: String, connection: Connection, newsPaperID: Int): Unit = {
    // TODO try catch
    val article = scrapeArticle(articleLink, cookie)

    // TODO add column for byline
    val author: Author = article.author

    val statement = connection.createStatement
    val rs = statement.executeQuery(
      s"""
        |INSERT INTO `article` (`title`, `articleText`, `section`, `author`, `url`, `publishedDate`, `newspaper_id`)
        |VALUES
        |	('${article.title}', '${article.text}', '${article.section}', '${author.name}', '${article.url}', '${article.date}', ${newsPaperID});
      """.stripMargin
    )
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

    println(s"Source: ${source}")
    println(s"Title: ${title}")
    println(s"Date: ${date}")
    println(s"Author: ${author}")
    println(s"Section: ${section}")
    println(s"Link: ${articleLink}")

    Article(articleText, source, title, date, author, section, articleLink)
  }
}
