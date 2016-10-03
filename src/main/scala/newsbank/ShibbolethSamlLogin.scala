package newsbank

import java.io.FileInputStream
import java.util.Properties

import org.jsoup.Connection.Method
import org.jsoup.{Connection, Jsoup}
import org.jsoup.nodes.Document
import scala.collection.JavaConverters._

/**
  * Performs a SAML login with the credentials provided in secrets.properties
  *
  * @author Kevin Chen
  */
class ShibbolethSamlLogin {

}

object ShibbolethSamlLogin {
  def main(args: Array[String]): Unit = {
    // POST https://idp.soas.ac.uk/shibboleth-idp/Authn/UserPassword

    val prop = new Properties()
    prop.load(getClass.getResourceAsStream("/secrets.properties"))

    val username = prop.getProperty("username")
    val password = prop.getProperty("password")

    var url = "https://idp.soas.ac.uk/shibboleth-idp/Authn/UserPassword"
    val res: Connection.Response = Jsoup.connect(url)
      .data("j_username", username)
      .data("j_password", password)
      .followRedirects(true)
      .method(Method.POST)
      .execute()

    println(s"Received code: ${res.statusCode()} from ${url}")
    printHeaders(res)
    var cookieMap: java.util.Map[String, String] = printCookies(res)
    println("")

    url = "https://idp.soas.ac.uk/shibboleth-idp/profile/Shibboleth/SSO"
    val res2 = Jsoup.connect(url)
      .cookies(cookieMap)
      .method(Method.GET)
      .execute()
    println(s"Received code: ${res2.statusCode()} from ${url}")
    printHeaders(res2)
    cookieMap.putAll(printCookies(res2))
    println("")

    url = "https://login.ezproxy.soas.ac.uk/Shibboleth.sso/SAML/POST"
    val res3 = Jsoup.connect(url)
      .data("j_username", username)
      .data("j_password", password)
      .cookies(cookieMap)
      .followRedirects(true)
      .method(Method.POST)
      .execute()
    println(s"Received code: ${res3.statusCode()} from ${url}")
    printHeaders(res3)
    cookieMap.putAll(printCookies(res3))
    println("")
  }

  def printHeaders(res: Connection.Response): Unit = {
    val headerMap = res.headers()
    val headers = headerMap.asScala
    println("Response headers...")
    headers.foreach(println)
  }

  def printCookies(res: Connection.Response): java.util.Map[String, String] = {
    val cookieMap = res.cookies()
    val cookies = cookieMap.asScala
    println("Response cookies...")
    cookies.foreach(println)
    cookieMap
  }
}