package org.example.com.http

import cats.data.ReaderT
import cats.effect.IO
import cats.implicits.toBifunctorOps
import org.example.com.context.ApplicationContext
import org.example.com.exception.{AppError, HtmlLoadError, TimeOutError}
import org.http4s.Header.Raw
import org.http4s.dsl.io.GET
import org.http4s.{Headers, Request, Uri}
import org.typelevel.ci.CIString

import java.util.concurrent.TimeoutException
import scala.concurrent.duration.DurationInt

trait HttpClient {
  def loadHtml(url: String): ReaderT[IO, ApplicationContext, Either[AppError, String]]
}

class HttpClientImpl extends HttpClient {
  override def loadHtml(url: String): ReaderT[IO, ApplicationContext, Either[AppError, String]] =
    for {
      context <- ReaderT.ask[IO, ApplicationContext]
      responseHtml <- requestForHtmlLoad(url) match {
        case Left(err) => ReaderT.pure[IO, ApplicationContext, Either[AppError, String]](Left(err))
        case Right(request) => ReaderT.liftF(context.httpClient
          .expect[String](request)
          .timeout(5.seconds)
          .attempt
          .map {
            case Left(err: TimeoutException) => Left(TimeOutError(message = s"Request TimeOut: ${err.getMessage}"))
            case Left(err) => Left(HtmlLoadError(message = err.getMessage))
            case Right(value) => Right(value)
          }
        )
      }
    } yield responseHtml

  private def requestForHtmlLoad(url: String): Either[AppError, Request[IO]] = {
    Uri.fromString(url).leftMap { er =>
      HtmlLoadError(s"Invalid url ${er.details}")
    }.map {uri =>
      Request[IO](
        method = GET,
        uri = uri,
        headers = Headers(
          Raw(CIString("User-Agent"), "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"),
          Raw(CIString("Accept"), "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
          Raw(CIString("Accept-Language"), "en-US,en;q=0.5"),
          Raw(CIString("Connection"), "keep-alive"),
          Raw(CIString("Upgrade-Insecure-Requests"), "1")
        )
      )
    }

  }

}
