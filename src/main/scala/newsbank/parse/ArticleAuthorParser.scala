package newsbank.parse

import org.jsoup.select.Elements

/**
  * @author Kevin Chen
  */
object ArticleAuthorParser {

  def parseAuthor(docHtml: Elements) = {
    val moreDetailsElement = docHtml.select("div.moredetails")
    val authorBylineElement = moreDetailsElement.select("li.author").select("span.val")
    val authorBylineText = authorBylineElement.text()

    var author: Option[String] = None
    var byline: Option[String] = None

    if (authorBylineText.contains("/")) {
      val authorBylineSplit = authorBylineText.split("/")
      author = Some(authorBylineSplit(0).trim)
      byline = Some(authorBylineSplit(1).trim)
    } else {
      author = Some(authorBylineText.trim)
    }

    Author(author.get, byline)
  }
}
