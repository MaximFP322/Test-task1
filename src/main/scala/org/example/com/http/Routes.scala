package org.example.com.http

import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.generic.auto._
import cats.effect.IO
import org.example.com.context.ApplicationContext
import org.example.com.model.ScrapeRequest
import org.example.com.service.RequestHandler
import org.http4s.circe.CirceEntityCodec._

object Routes {
  def routes(applicationContext: ApplicationContext): HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "scrape" =>
        RequestHandler.parseAndExecuteRequest(req) { parseReq =>
          for {
            _ <- applicationContext.logger.info(s"Request: $parseReq")
            result <- applicationContext.responseService.scrapeExecute(parseReq).run(applicationContext)
          } yield result
        }
    }
  }
}
