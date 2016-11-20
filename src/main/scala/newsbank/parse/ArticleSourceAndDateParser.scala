package newsbank.parse

import java.text.SimpleDateFormat

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
    val sqlDate = reformatDateToSqlFormat(date)

    (source, sqlDate)
  }

  def reformatDateToSqlFormat(date_s: String) = {
    val dt = new SimpleDateFormat("MMMM dd, yyyy")
    val date = dt.parse(date_s)
    val dt1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    dt1.format(date)
  }

  def main(args: Array[String]): Unit = {
    val date_s = "November 14, 2016"
    println(reformatDateToSqlFormat(date_s))
  }
}
