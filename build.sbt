ThisBuild / scalaVersion := "3.1.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.sworliteary"
ThisBuild / organizationName := "sworliteary"

val circeVersion = "0.14.2"

lazy val root = (project in file("."))
  .settings(
    name := "work-sayonara-voyage",
    libraryDependencies := Seq(
      "commons-io" % "commons-io" % "2.8.0",
      "joda-time" % "joda-time" % "2.10.14",
      "com.github.scopt" %% "scopt" % "4.0.1"
    ) ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
  )
