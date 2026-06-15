sbtPlugin   := true
name        := "sbt-webjars"
organization := "org.webjars"

scalaVersion := "2.12.20"

crossScalaVersions := Seq("2.12.20", "3.8.4")

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.12.11"
    case _      => "2.0.0"
  }
}

licenses := Seq("MIT License" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/webjars/sbt-webjars"))

developers := List(
  Developer(
    "jamesward",
    "James Ward",
    "james@jamesward.com",
    url("https://jamesward.com")
  )
)

ThisBuild / versionScheme := Some("semver-spec")
