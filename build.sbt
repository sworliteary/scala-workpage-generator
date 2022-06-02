ThisBuild / scalaVersion := "3.1.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.sworliteary"
ThisBuild / organizationName := "sworliteary"

lazy val root = (project in file("."))
  .settings(
    name := "work-sayonara-voyage",
    libraryDependencies := Seq(
        "com.github.scopt" %% "scopt" % "4.0.1"
    )
  )
