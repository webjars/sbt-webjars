// Regression test for the sbt 2.x action-cache failure mode where
// `webJarsGenerate` is memoized with a stale `Seq[File]` after its
// generated source disappears from disk. Symptom in the wild: a
// long-lived `~Test/runReload` session intermittently fails with
// "Not found: webjars" because the body of `webJarsGenerate` was
// skipped on a cache hit and the file wasn't restored from disk.

scalaVersion := "3.8.4"

libraryDependencies ++= Seq(
  "org.webjars.npm" % "bootstrap" % "5.3.8" % Set(WebJar, Compile),
  "org.webjars"     % "jquery"    % "3.7.1" % Set(WebJar, Test),
)

lazy val checkExists       = taskKey[Unit]("Assert WebJars.scala exists")
lazy val checkNotExists    = taskKey[Unit]("Assert WebJars.scala does NOT exist")
lazy val deleteWebJars     = taskKey[Unit]("Delete the generated WebJars.scala")
lazy val checkContentMatches = taskKey[Unit]("Assert WebJars.scala has the bootstrap entry on disk")
lazy val invokeGen         = taskKey[Unit]("Invoke webJarsGenerate without going through compile")

// Side effects must be uncached -- otherwise sbt 2.x's action cache will
// memoize the no-op result and skip the body on subsequent invocations.
checkExists := Def.uncached {
  val src = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  assert(src.exists, s"Expected generated source at $src")
}

checkNotExists := Def.uncached {
  val src = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  assert(!src.exists, s"Expected $src to be absent")
}

deleteWebJars := Def.uncached {
  val src = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  if (src.exists) IO.delete(src)
}

checkContentMatches := Def.uncached {
  val src = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  assert(src.exists, s"Missing $src")
  val s = IO.read(src)
  assert(s.contains("""case bootstrap extends Artifact("org.webjars.npm", "bootstrap", "5.3.8")"""),
    s"Generated source missing expected bootstrap entry. Content was:\n$s")
}

// Invoke the generator directly (without going through compile) to isolate
// the action-cache behavior from any compile-driven file-restore behavior.
invokeGen := Def.uncached {
  val _ = webJarsGenerate.value
}
