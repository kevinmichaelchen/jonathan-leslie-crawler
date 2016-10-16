package newsbank

/**
  * @author Kevin Chen
  */
case class Field(field: String, value: String, boolean: Option[String] = None, nextField: Option[Field] = None)

case class Link(
                 rootUrl: String,
                 p: String,
                 b: String,
                 action: String,
                 t: String,
                 field: Field,
                 sort: String
               )

object Links {
  val rootUrl = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb"

  // fld0=alltext&val0=iran&bln1=AND&fld1=YMD_date&val1=&sort=YMD_date%3AD
  def formatUrl(link: Link): String = {
    val sb = new StringBuilder(s"${link.rootUrl}?p=${link.p}&b=${link.b}&action=${link.action}&t=${link.t}")

    def appendField(sb: StringBuilder, field: Field, i: Int): Unit = {
      sb.append(s"&fld${i}=${field.field}&val${i}=${field.value}")
      if (field.boolean.isDefined) {
        sb.append(s"&bln${i + 1}=${field.boolean.get}")
        appendField(sb, field.nextField.get, i + 1)
      }
    }

    appendField(sb, link.field, 0)

    sb.append(s"&sort=${link.sort}")
    sb.append("&maxresults=50")
    sb.toString()
  }

  val jerusalemPostIranArticles = Link(
    rootUrl,
    "AWNB",
    "results",
    "search",
    "continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIsrael%2Band%2BPalestinian%2BTerritories%21Israel%2Band%2BPalestinian%2BTerritories%2Fpubname%3AJERB%21Jerusalem%2BPost%252C%2BThe%2B%2528Israel%2529",
    Field(
      "alltext",
      "iran",
      Some("AND"),
      Some(
        Field(
          "YMD_date",
          ""
        )
      )
    ),
    "YMD_date%3AD"
  )

  val jerusalemPostIranOpinion = Link(
    rootUrl,
    "AWNB",
    "results",
    "search",
    "continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIsrael%2Band%2BPalestinian%2BTerritories%21Israel%2Band%2BPalestinian%2BTerritories%2Fpubname%3AJERB%21Jerusalem%2BPost%252C%2BThe%2B%2528Israel%2529",
    Field(
      "alltext",
      "iran",
      Some("AND"),
      Some(
        Field(
          "Section",
          "OPINION"
        )
      )
    ),
    "YMD_date%3AD"
  )

  val jerusalemPostIranNuclearOpinion = Link(
    rootUrl,
    "AWNB",
    "results",
    "search",
    "continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIsrael%2Band%2BPalestinian%2BTerritories%21Israel%2Band%2BPalestinian%2BTerritories%2Fpubname%3AJERB%21Jerusalem%2BPost%252C%2BThe%2B%2528Israel%2529",
    Field(
      "alltext",
      "iran",
      Some("AND"),
      Some(
        Field(
          "Section",
          "OPINION",
          Some("AND"),
          Some(
            Field(
              "alltext",
              "nuclear"
            )
          )
        )
      )
    ),
    "YMD_date%3AD"
  )

  val tehranTimesIsraelZionistArticles = Link(
    rootUrl,
    "AWNB",
    "results",
    "search",
    "continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3ATRTB%21Tehran%2BTimes%2B%2528Iran%2529",
    Field(
      "alltext",
      "israel",
      Some("OR"),
      Some(
        Field(
          "alltext",
          "zionist"
        )
      )
    ),
    "YMD_date%3AA"
  )

  val tehranTimesIsraelOpinion = Link(
    rootUrl,
    "AWNB",
    "results",
    "search",
    "continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3ATRTB%21Tehran%2BTimes%2B%2528Iran%2529",
    Field(
      "alltext",
      "israel",
      Some("AND"),
      Some(
        Field(
          "Section",
          "Opinion"
        )
      )
    ),
    "YMD_date%3AA"
  )

  val iranNewsIsraelZionistArticles = Link(
    rootUrl,
    "AWNB",
    "results",
    "search",
    "continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3AIRNB%21Iran%2BNews%2B%2528Iran%2529",
    Field(
      "alltext",
      "israel",
      Some("OR"),
      Some(
        Field(
          "alltext",
          "zionist"
        )
      )
    ),
    "YMD_date%3AA"
  )

  val iranDailyIsraelZionistArticles = Link(
    rootUrl,
    "AWNB",
    "results",
    "search",
    "continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3AIID7%21Iran%2BDaily%2B%2528Tehran%252C%2BIran%2529",
    Field(
      "alltext",
      "israel",
      Some("OR"),
      Some(
        Field(
          "alltext",
          "zionist"
        )
      )
    ),
    "YMD_date%3AA"
  )

  val mojNewsIsraelZionistArticles = Link(
    rootUrl,
    "AWNB",
    "results",
    "search",
    "continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3AMNAT%21Moj%2BNews%2BAgency%2B%2528Tehran%252C%2BIran%2529",
    Field(
      "alltext",
      "israel",
      Some("OR"),
      Some(
        Field(
          "alltext",
          "zionist"
        )
      )
    ),
    "YMD_date%3AA"
  )

  def main(args: Array[String]): Unit = {
    var expected = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb?p=AWNB&b=results&action=search&t=continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIsrael%2Band%2BPalestinian%2BTerritories%21Israel%2Band%2BPalestinian%2BTerritories%2Fpubname%3AJERB%21Jerusalem%2BPost%252C%2BThe%2B%2528Israel%2529&fld0=alltext&val0=iran&bln1=AND&fld1=YMD_date&val1=&sort=YMD_date%3AD&maxresults=50"
    var actual = formatUrl(jerusalemPostIranArticles)
    println(expected)
    println(actual)
    println(expected equals actual)

    expected = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb?p=AWNB&b=results&action=search&t=continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIsrael%2Band%2BPalestinian%2BTerritories%21Israel%2Band%2BPalestinian%2BTerritories%2Fpubname%3AJERB%21Jerusalem%2BPost%252C%2BThe%2B%2528Israel%2529&fld0=alltext&val0=iran&bln1=AND&fld1=Section&val1=OPINION&sort=YMD_date%3AD&maxresults=50"
    actual = formatUrl(jerusalemPostIranOpinion)
    println(expected)
    println(actual)
    println(expected equals actual)

    expected = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb?p=AWNB&b=results&action=search&t=continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIsrael%2Band%2BPalestinian%2BTerritories%21Israel%2Band%2BPalestinian%2BTerritories%2Fpubname%3AJERB%21Jerusalem%2BPost%252C%2BThe%2B%2528Israel%2529&fld0=alltext&val0=iran&bln1=AND&fld1=Section&val1=OPINION&bln2=AND&fld2=alltext&val2=nuclear&sort=YMD_date%3AD&maxresults=50"
    actual = formatUrl(jerusalemPostIranNuclearOpinion)
    println(expected)
    println(actual)
    println(expected equals actual)

    expected = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb?p=AWNB&b=results&action=search&t=continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3ATRTB%21Tehran%2BTimes%2B%2528Iran%2529&fld0=alltext&val0=israel&bln1=OR&fld1=alltext&val1=zionist&sort=YMD_date%3AA&maxresults=50"
    actual = formatUrl(tehranTimesIsraelZionistArticles)
    println(expected)
    println(actual)
    println(expected equals actual)

    expected = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb?p=AWNB&b=results&action=search&t=continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3ATRTB%21Tehran%2BTimes%2B%2528Iran%2529&fld0=alltext&val0=israel&bln1=AND&fld1=Section&val1=Opinion&sort=YMD_date%3AA&maxresults=50"
    actual = formatUrl(tehranTimesIsraelOpinion)
    println(expected)
    println(actual)
    println(expected equals actual)

    expected = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb?p=AWNB&b=results&action=search&t=continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3AIRNB%21Iran%2BNews%2B%2528Iran%2529&fld0=alltext&val0=israel&bln1=OR&fld1=alltext&val1=zionist&sort=YMD_date%3AA&maxresults=50"
    actual = formatUrl(iranNewsIsraelZionistArticles)
    println(expected)
    println(actual)
    println(expected equals actual)

    expected = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb?p=AWNB&b=results&action=search&t=continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3AIID7%21Iran%2BDaily%2B%2528Tehran%252C%2BIran%2529&fld0=alltext&val0=israel&bln1=OR&fld1=alltext&val1=zionist&sort=YMD_date%3AA&maxresults=50"
    actual = formatUrl(iranDailyIsraelZionistArticles)
    println(expected)
    println(actual)
    println(expected equals actual)

    expected = "http://infoweb.newsbank.com.ezproxy.soas.ac.uk/resources/search/nb?p=AWNB&b=results&action=search&t=continent%3AMiddle%2BEast%21Middle%2BEast%2Fcountry%3AIran%21Iran%2Fpubname%3AMNAT%21Moj%2BNews%2BAgency%2B%2528Tehran%252C%2BIran%2529&fld0=alltext&val0=israel&bln1=OR&fld1=alltext&val1=zionist&sort=YMD_date%3AA&maxresults=50"
    actual = formatUrl(mojNewsIsraelZionistArticles)
    println(expected)
    println(actual)
    println(expected equals actual)
  }
}
