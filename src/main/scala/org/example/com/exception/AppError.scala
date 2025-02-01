package org.example.com.exception

sealed trait AppError {
  val message: String
}

case class TitleNotFoundError(message: String) extends AppError
case class HtmlLoadError(message: String) extends AppError
case class TimeOutError(message: String) extends AppError