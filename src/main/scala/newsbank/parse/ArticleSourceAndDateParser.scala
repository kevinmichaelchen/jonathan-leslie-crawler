package newsbank.parse

import org.jsoup.select.Elements

/**
  * @author Kevin Chen
  */
object ArticleSourceAndDateParser {
  def parseSourceAndDate(docHtml: Elements): (String, String) = {
    val sourceElement = docHtml.select("div.source")
    val sourceText = sourceElement.text()
    val split = sourceText.split("-")
    val source = split(0).trim
    val dateRegex = "(.*\\d\\d\\d\\d)(.*)".r
    val date = dateRegex.findFirstMatchIn(split(1).trim).get.group(1)
    (source, date)
  }
}
