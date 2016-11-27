package util

import java.io.File
import java.nio.charset.StandardCharsets

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.exception.ExceptionUtils

/**
  * @author Kevin Chen
  */
object ExceptionFileLogger {

  def log(string: String, file: File): Unit = {
    try {
      FileUtils.write(file, string, StandardCharsets.UTF_8, true)
    } catch {
      case e: Exception => {}
    }
  }

  def log(throwable: Throwable, file: File) {

    try {
      val stackTrace = ExceptionUtils.getStackTrace(throwable)
      log(stackTrace, file)
    } catch {
      case e: Exception => {}
    }
  }
}
