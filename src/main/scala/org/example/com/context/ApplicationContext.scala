package org.example.com.context

import cats.effect.IO
import cats.effect.kernel.Resource
import org.example.com.http.{HttpClient, HttpClientImpl}
import org.example.com.service.{HtmlParser, HtmlParserImpl, ResponseService, ResponseServiceImpl}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax.CatsEffectConfigSource

final case class ServerConfig(host: String, port: Int, executionParallel: Int)
final case class ApplicationConfig(serverConfig: ServerConfig)
final case class ApplicationContext(
  applicationConfig: ApplicationConfig,
  httpClient: Client[IO],
  httpService: HttpClient,
  htmlParser: HtmlParser,
  responseService: ResponseService,
  logger: Logger[IO]
)

object ApplicationContext {
  private def loadConfig: IO[ApplicationConfig] = ConfigSource.default.loadF[IO, ApplicationConfig]

  def contextInit: Resource[IO, ApplicationContext] = for {
    config <- Resource.eval[IO, ApplicationConfig](loadConfig)
    client <- EmberClientBuilder.default[IO].build
    applicationContext = ApplicationContext(
      applicationConfig = config,
      httpClient = client,
      httpService = new HttpClientImpl,
      htmlParser = new HtmlParserImpl,
      responseService = new ResponseServiceImpl,
      logger = Slf4jLogger.getLogger[IO]
    )
  } yield applicationContext
}
