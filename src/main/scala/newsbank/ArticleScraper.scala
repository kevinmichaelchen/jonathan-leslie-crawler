package newsbank

import newsbank.parse.{ArticleDateParser, ArticleTextParser, ArticleTitleParser}

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

    val moreDetailsElement = docHtml.select("div.moredetails")
    val authorBylineElement = moreDetailsElement.select("li.author").select("span.val")
    val authorBylineText = authorBylineElement.text()

    var author: Option[String] = None
    var byline: Option[String] = None

    if (authorBylineText.contains("/")) {
      val authorBylineSplit = authorBylineText.split("/")
      author = Some(authorBylineSplit(0))
      byline = Some(authorBylineSplit(1))
    } else {
      author = Some(authorBylineText.trim)
    }

    val sectionElement = moreDetailsElement.select("li.section").select("span.val")
    val section = sectionElement.text()

    Main.numArticlesScraped += 1
//    println(s"Text: ${articleText}")
    println(s"Title: ${title}")
    println(s"Date: ${date}")
    println(s"Author: ${author.get}")
    println(s"Byline: ${byline}")
    println(s"Section: ${section}")
    println(articleLink)
  }
}
