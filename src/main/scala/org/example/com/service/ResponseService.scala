package org.example.com.service

import cats.data.ReaderT
import cats.effect.IO
import cats.implicits.toBifunctorOps
import fs2._
import org.example.com.context.ApplicationContext
import org.example.com.exception.{HtmlLoadError, TitleNotFoundError}
import org.example.com.model.{ScrapeRequest, ScrapeResponse, TitleParseResult}

trait ResponseService {
  def scrapeExecute(request: ScrapeRequest): ReaderT[IO, ApplicationContext, ScrapeResponse]
}

class ResponseServiceImpl extends ResponseService {
  override def scrapeExecute(request: ScrapeRequest): ReaderT[IO, ApplicationContext, ScrapeResponse] =
    for {
      context <- ReaderT.ask[IO, ApplicationContext]
      resultList <- ReaderT.liftF {
        Stream
          .emits(request.urls)
          .covary[IO]
          .parEvalMap(context.applicationConfig.serverConfig.executionParallel)(link => linkExecute(link).run(context))
          .compile
          .toList
      }
    } yield ScrapeResponse(results = resultList)

  private def linkExecute(link: String): ReaderT[IO, ApplicationContext, TitleParseResult] =
    for {
      context <- ReaderT.ask[IO, ApplicationContext]
      result <- context.httpService.loadHtml(link)
        .map { html =>
          html
            .flatMap(h => context.htmlParser.titleParse(h))
            .fold(
              err => TitleParseResult.Failure(link, err.message),
              title => TitleParseResult.Success(link, title)
            )
        }
    } yield result

}
