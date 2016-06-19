# Getting started
## Installing Dependencies
This project requires Scala and MongoDB
```
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