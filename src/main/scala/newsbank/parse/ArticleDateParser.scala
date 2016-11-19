package newsbank.parse

import org.jsoup.select.Elements

/**
  * @author Kevin Chen
  */
object ArticleDateParser {
  def parseDate(docHtml: Elements) = {
    val sourceElement = docHtml.select("div.source")
    val sourceText = sourceElement.text()
    val split = sourceText.split("-")
    val source = split(0).trim
    val dateRegex = "(.*\\d\\d\\d\\d)(.*)".r
    val date = dateRegex.findFirstMatchIn(split(1).trim).get.group(1)
    println(s"Source: ${source}")
    date
  }
}
