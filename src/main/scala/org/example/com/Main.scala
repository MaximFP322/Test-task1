package org.example.com

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.comcast.ip4s.{Host, IpLiteralSyntax, Port}
import org.example.com.context.ApplicationContext
import org.example.com.http.Routes
import org.http4s.ember.server.EmberServerBuilder

object Main extends IOApp{
  override def run(args: List[String]): IO[ExitCode] = initApp.useForever.as(ExitCode.Success)

  private def initApp: Resource[IO, Unit] =
    for {
      applicationContext <- ApplicationContext.contextInit
      host <- Resource.eval(
        IO.fromOption(Host.fromString(applicationContext.applicationConfig.serverConfig.host))(
          new Exception(s"Некорректный host: ${applicationContext.applicationConfig.serverConfig.host}")
        )
      )
      port <- Resource.eval(
        IO.fromOption(Port.fromInt(applicationContext.applicationConfig.serverConfig.port))(
          new Exception(s"Некорректный port: ${applicationContext.applicationConfig.serverConfig.port}")
        )
      )
      _ <- EmberServerBuilder
        .default[IO]
        .withHttpApp(Routes.routes(applicationContext).orNotFound)
        .withHost(host)
        .withPort(port)
        .build
    } yield ()


}
