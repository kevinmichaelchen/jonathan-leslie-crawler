package newsbank.parse

import org.jsoup.select.Elements

/**
  * @author Kevin Chen
  */
object ArticleTitleParser {
  def parseTitle(docHtml: Elements) = {
    val titleElement = docHtml.select("div.title h2")
    val title = titleElement.text()
    title
  }
}
