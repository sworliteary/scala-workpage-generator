ThisBuild / scalaVersion := "3.1.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.sworliteary"
ThisBuild / organizationName := "sworliteary"

val circeVersion = "0.14.2"

lazy val root = (project in file("."))
  .settings(
    name := "work-sayonara-voyage",
    nativeImageOptions ++= List(
      "--initialize-at-build-time",
      "--no-fallback",
      "--no-server"
    ),
    libraryDependencies := Seq(
      "commons-io" % "commons-io" % "2.8.0",
      "joda-time" % "joda-time" % "2.10.14",
      "com.github.scopt" %% "scopt" % "4.0.1",
      "com.typesafe.play" % "twirl-api_3" % "1.6.0-M6",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ) ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
  )
  .enablePlugins(SbtTwirl)
  .enablePlugins(NativeImagePlugin)
