package newsbank

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
  * @author Kevin Chen
  */
object HttpGetter {
  def get(url: String, cookie: String): Document =
    Jsoup.connect(url)
      .timeout(6000)
      .cookie("ezproxy", cookie)
      // TODO fix timeout error
      .get
}
