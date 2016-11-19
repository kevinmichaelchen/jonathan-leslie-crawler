package newsbank

/**
  * @author Kevin Chen
  */
object ArticleScraper {

  def scrapeArticle(articleLink: String, cookie: String): Unit = {
    val doc = HttpGetter.get(articleLink, cookie)
    val docHtml = doc.select("div.nb-doc")

    val articleTextElement = docHtml.select("div.body")
    val articleText = articleTextElement.text()

    val titleElement = docHtml.select("div.title h2")
    val title = titleElement.text()

    val sourceElement = docHtml.select("div.source")
    val sourceText = sourceElement.text()
    val split = sourceText.split("-")
    val source = split(0).trim
    val dateRegex = "(.*\\d\\d\\d\\d)(.*)".r
    val date = dateRegex.findFirstMatchIn(split(1).trim).get.group(1)

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
    println(s"Text: ${articleText}")
    println(s"Source: ${source}")
    println(s"Title: ${title}")
    println(s"Date: ${date}")
    println(s"Author: ${author.get}")
    println(s"Byline: ${byline}")
    println(s"Section: ${section}")
    println(articleLink)
  }
}
