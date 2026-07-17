// A project with no WebJar-scoped dependencies must get no generated
// sources: no `WebJars.scala`, and an empty `webJarsGenerate` result.

scalaVersion := "3.8.4"

lazy val checkNoGeneratedSources = taskKey[Unit](
  "Assert webJarsGenerate returns nothing and WebJars.scala is absent")
lazy val seedStaleWebJars = taskKey[Unit](
  "Plant a stale WebJars.scala as if a previous run had WebJar deps")

// Side effects must be uncached -- otherwise sbt 2.x's action cache will
// memoize the no-op result and skip the body on subsequent invocations.
checkNoGeneratedSources := Def.uncached {
  val generated = webJarsGenerate.value
  assert(generated.isEmpty,
    s"Expected no generated sources without WebJar deps, got: $generated")
  val src = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  assert(!src.exists, s"Expected no generated source at $src")
}

seedStaleWebJars := Def.uncached {
  val src = (Compile / sourceManaged).value / "webjars" / "WebJars.scala"
  IO.write(src, "package webjars.generated\n\nobject WebJars\n")
}
