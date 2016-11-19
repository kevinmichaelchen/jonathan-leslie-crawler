package newsbank.parse

import org.jsoup.select.Elements

/**
  * @author Kevin Chen
  */
object ArticleTextParser {
  def parseText(docHtml: Elements) = {
    val articleTextElement = docHtml.select("div.body")
    val articleText = articleTextElement.text()
    articleText
  }
}
