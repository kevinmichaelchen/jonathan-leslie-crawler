package newsbank.parse

/**
  * @author Kevin Chen
  */
case class Author(name: String, byline: Option[String])
case class Article(text: String, source: String, title: String, date: String, author: Author, section: String, url: String)
