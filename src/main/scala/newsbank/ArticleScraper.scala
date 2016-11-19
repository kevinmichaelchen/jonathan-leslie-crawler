package newsbank

import newsbank.parse._

/**
  * @author Kevin Chen
  */
object ArticleScraper {

  def scrapeArticle(articleLink: String, cookie: String): Unit = {
    val doc = HttpGetter.get(articleLink, cookie)
    val docHtml = doc.select("div.nb-doc")
    if (docHtml.isEmpty) {
      throw new IllegalStateException(s"div.nb-doc not found on article: ${articleLink}")
    }

    val articleText = ArticleTextParser.parseText(docHtml)

    val title = ArticleTitleParser.parseTitle(docHtml)

    val date = ArticleDateParser.parseDate(docHtml)

    val author: Author = ArticleAuthorParser.parseAuthor(docHtml)

    val section = ArticleSectionParser.parseSection(docHtml)

    Main.numArticlesScraped += 1
    println(s"Title: ${title}")
    println(s"Date: ${date}")
    println(s"Author: ${author}")
    println(s"Section: ${section}")
    println(articleLink)
  }
}
