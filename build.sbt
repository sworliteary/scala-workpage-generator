ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.sworliteary"
ThisBuild / organizationName := "sworliteary"

lazy val root = (project in file("."))
  .settings(
    name := "work-sayonara-voyage",
    libraryDependencies := Seq(
        "com.github.scopt" %% "scopt" % "3.7.1",
        "com.github.eikek" %% "yamusca-core" % "0.8.0"
    )
  )
