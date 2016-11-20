# Background
## Kayhan
Kayhan is an Iranian newspaper. Scraping was relatively straight-forward. Took a day.

## Newsbank
Newsbank is SAML-secured, but we boot on implementing the whole dance to retrieve the ezproxy cookie and simply supply the cookie ahead of time in a properties file.

```mysql
CREATE TABLE `newspaper` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `article` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL DEFAULT '',
  `articleText` mediumtext NOT NULL,
  `section` varchar(50) NOT NULL,
  `byline` varchar(50) NOT NULL,
  `author` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `publishedDate` datetime NOT NULL,
  `newspaper_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_article_newspaper_id` (`newspaper_id`),
  CONSTRAINT `FK_article_newspaper_id` FOREIGN KEY (`newspaper_id`) REFERENCES `newspaper` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `newspaper` (`id`, `name`)
VALUES
  (1, 'Jerusalem Post, The (Israel)');
```

# Getting started
## Installing Dependencies
This project requires [Scala](http://www.scala-lang.org/), [SBT](http://www.scala-sbt.org/) (Scala Build Tool), and [MongoDB](https://www.mongodb.com/).
```bash
# installing mongo can take a while
brew install scala sbt mongodb

brew services start mongodb
```

It is also possible to [configure SBT to run a specific main method](https://www.safaribooksonline.com/library/view/scala-cookbook/9781449340292/ch18s10.html), 
though I just build and run inside of IntelliJ.

## Using the `mongo` command
Entering `mongo` in Terminal will put you in a mongo shell 
```
use jonathan_leslie
db.kayhan_article.find().pretty()
```

To wipe your DB, you'd run `db.kayhan_article.deleteMany({})`

## Exporting scraped data

### To JSON
```bash
mongoexport --db jonathan_leslie --collection kayhan_article --pretty --out article.json
```

### To CSV
```bash
mongoexport --db jonathan_leslie --collection kayhan_article --csv --fields day,month,year,title,subtitle,body,url --out article.csv
```