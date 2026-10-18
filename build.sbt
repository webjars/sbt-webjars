enablePlugins(SbtPlugin)
name        := "sbt-webjars"
organization := "org.webjars"

scalaVersion := "2.12.21"

crossScalaVersions := Seq("2.12.21", "3.8.4")

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.13.0"
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

addSbtPlugin("com.github.sbt" % "sbt2-compat" % "0.2.0")

javacOptions ++= Seq("-source", "17", "-target", "17")
scalacOptions ++= (scalaBinaryVersion.value match {
  case "2.12" => Seq.empty // Scala 2.12 cannot target > JDK 8
  case _      => Seq("-release", "17")
})

scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
scriptedBufferLog := false

// SkillsJars: extract agent Skills with `./sbta extractSkillsJars`
skillsJarsOutputDir := Some(file(".kiro/skills"))

libraryDependencies += "com.jamesward" % "skills" % "0.0.13" % Skills
