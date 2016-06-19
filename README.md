# Background
...

# Getting started
## Installing Dependencies
This project requires [Scala](http://www.scala-lang.org/) and [MongoDB](https://www.mongodb.com/).
```bash
# installing mongo can take a while
brew install scala mongodb

brew services start mongodb
```

## Using the `mongo` command
Entering `mongo` in Terminal will put you in a mongo shell 
```
use jonathan_leslie
db.kayhan_article.find().pretty()
```

## Exporting scraped data

### To JSON
```bash
mongoexport --db jonathan_leslie --collection kayhan_article --pretty --out article.json
```

### To CSV
```bash
mongoexport --db jonathan_leslie --collection kayhan_article --csv --fields day,month,year,title,subtitle,body,url --out article.csv
```