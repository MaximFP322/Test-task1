package org.example.com.model

sealed trait TitleParseResult {
  val urlRequest: String
}

object TitleParseResult {
  final case class Success(urlRequest: String, title: String) extends TitleParseResult
  final case class Failure(urlRequest: String, errorMessage: String) extends TitleParseResult
}
