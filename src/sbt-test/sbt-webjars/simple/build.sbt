lazy val check = taskKey[Unit]("Verify the generated WebJars locator")
lazy val recordWebJarsMtime = taskKey[Unit]("Record the WebJars.scala mtime")
lazy val checkWebJarsUntouched = taskKey[Unit]("Assert WebJars.scala mtime did not change")

scalaVersion := "3.8.4"

libraryDependencies ++= Seq(
  "org.webjars.npm" % "bootstrap" % "5.3.8" % Set(WebJar, Compile),
  "org.webjars"     % "jquery"    % "3.7.1" % Set(WebJar, Test),
)

check := {
  val _   = (Compile / compile).value
  val src = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  assert(src.exists, s"Expected generated source at $src")

  val content = IO.read(src)

  // Bootstrap is in WebJar+Compile; jQuery is in WebJar+Test. Both should
  // appear in the locator because the generator reads the WebJar config only.
  val bootstrapCase =
    """case bootstrap extends Artifact("org.webjars.npm", "bootstrap", "5.3.8")"""
  val jqueryCase =
    """case jquery extends Artifact("org.webjars", "jquery", "3.7.1")"""

  assert(content.contains(bootstrapCase),
    s"Missing bootstrap enum case in generated source:\n$content")
  assert(content.contains(jqueryCase),
    s"Missing jquery enum case in generated source:\n$content")
  assert(content.contains("def cdnUrl"), "Missing cdnUrl helper")
  assert(content.contains("def localUrl"), "Missing localUrl helper")
  assert(content.contains("def url"),     "Missing url helper")
}

// Records the mtime of the generated locator into target/webjars-mtime so
// a follow-up task can verify the file is left untouched on a no-op
// regenerate (the user-facing symptom of unnecessary recompilation).
recordWebJarsMtime := {
  val src = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  assert(src.exists, s"Expected generated source at $src")
  val marker = target.value / "webjars-mtime"
  IO.write(marker, src.lastModified.toString)
}

checkWebJarsUntouched := {
  val _      = webJarsGenerate.value
  val src    = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  val marker = target.value / "webjars-mtime"
  assert(marker.exists, s"recordWebJarsMtime must run before checkWebJarsUntouched")
  val before = IO.read(marker).trim.toLong
  val after  = src.lastModified
  assert(before == after,
    s"WebJars.scala was rewritten on a no-op regenerate (mtime $before -> $after)")
}
