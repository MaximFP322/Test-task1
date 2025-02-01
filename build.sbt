ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "test-task"
  )

val Http4sVersion = "0.23.30"
val CirceVersion  = "0.14.10"
val PureConfigVersion = "0.17.8"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
  "org.typelevel" %% "cats-effect"          % "3.5.7",
  "co.fs2"        %% "fs2-core"            % "3.11.0",
  "org.jsoup"     % "jsoup" % "1.18.3",
  "org.http4s"    %% "http4s-ember-server" % Http4sVersion,
  "org.http4s"    %% "http4s-ember-client" % Http4sVersion,
  "org.http4s"    %% "http4s-dsl"          % Http4sVersion,
  "org.http4s"    %% "http4s-circe"        % Http4sVersion,
  "io.circe"      %% "circe-generic"       % CirceVersion,
  "io.circe"      %% "circe-parser"        % CirceVersion,
  "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
  "ch.qos.logback" % "logback-classic" % "1.5.16"
)