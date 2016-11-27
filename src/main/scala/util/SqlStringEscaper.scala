package util

/**
  * @author Kevin Chen
  */
object SqlStringEscaper {
  def escape(string: String) = string.replaceAll("'", "''")
}
