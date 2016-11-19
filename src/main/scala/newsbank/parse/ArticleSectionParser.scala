package newsbank.parse

import org.jsoup.select.Elements

/**
  * @author Kevin Chen
  */
object ArticleSectionParser {
  def parseSection(docHtml: Elements) = {
    val moreDetailsElement = docHtml.select("div.moredetails")
    val sectionElement = moreDetailsElement.select("li.section").select("span.val")
    val section = sectionElement.text()
    section
  }
}
