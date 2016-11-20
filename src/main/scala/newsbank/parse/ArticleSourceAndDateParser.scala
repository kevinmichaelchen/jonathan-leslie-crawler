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

    // November 20, 2016
    val date = dateRegex.findFirstMatchIn(split(1).trim).get.group(1)

    // Format it as 2016-11-20 12:06:00

    (source, date)
  }

  def main(args: Array[String]): Unit = {

  }
}
