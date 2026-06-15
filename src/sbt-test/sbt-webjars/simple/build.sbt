lazy val check = taskKey[Unit]("Verify the generated WebJars locator")

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
