name := "jonathan-leslie-crawler"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.jsoup" % "jsoup" % "1.9.2"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.1.1"

// set the main class for 'sbt run'
mainClass in (Compile, run) := Some("kayhan.KayhanCrawler")