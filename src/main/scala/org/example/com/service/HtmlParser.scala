package org.example.com.service

import org.example.com.exception.{AppError, TitleNotFoundError}
import org.jsoup.Jsoup

trait HtmlParser {
  def titleParse(html: String): Either[AppError, String]
}

class HtmlParserImpl extends HtmlParser {
  override def titleParse(html: String): Either[TitleNotFoundError, String] = {
    Option(Jsoup.parse(html).select("title").first()).map(_.text()) match {
      case Some(value) => Right(value)
      case None => Left(TitleNotFoundError(message = "Title not found"))
    }
  }
}
