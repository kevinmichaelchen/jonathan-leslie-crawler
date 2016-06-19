package kayhan

import org.mongodb.scala.bson.collection.mutable.Document
import org.mongodb.scala.{Completed, MongoClient, MongoCollection, MongoDatabase, Observer}

/**
  * @author Kevin Chen
  */
object MongoPersister {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("jonathan_leslie")
  val collection: MongoCollection[Document] = database.getCollection("kayhan_article");

  def persist(article: Article): Unit = {
    val doc: Document = Document(
      "day" -> article.day,
      "month" -> article.month,
      "year" -> article.year,
      "title" -> article.title,
      "subtitle" -> article.subtitle,
      "body" -> article.body
    )

    // The insert operation won't occur until we subscribe
    collection.insertOne(doc).subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("Inserted")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }
}
