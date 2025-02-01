package org.example.com.service

import org.http4s.dsl.io._
import cats.effect.IO
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import org.example.com.model.{ScrapeRequest, ScrapeResponse}
import org.http4s.{Request, Response}
import org.http4s.circe.CirceEntityCodec._

object RequestHandler {
  def parseAndExecuteRequest(req: Request[IO])(onSuccess: ScrapeRequest => IO[ScrapeResponse]): IO[Response[IO]] = {
    req.as[ScrapeRequest].attempt.flatMap {
      case Left(err) => BadRequest(s"Invalid Json ${err.getMessage}")
      case Right(scrapeRequest) => Ok(onSuccess(scrapeRequest))
    }
  }

}
