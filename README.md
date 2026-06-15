sbt-webjars
===========

An sbt plugin for declaring [WebJar](https://www.webjars.org/) dependencies and generating a type-safe Scala locator for them.

[![Latest Release](https://img.shields.io/maven-central/v/org.webjars/sbt-webjars_2.12_1.0.svg)](https://mvnrepository.com/artifact/org.webjars/sbt-webjars) [![Test](https://github.com/webjars/sbt-webjars/actions/workflows/test.yml/badge.svg)](https://github.com/webjars/sbt-webjars/actions/workflows/test.yml)

What it does
------------

- Adds a hidden `webjar` Ivy configuration so WebJar JARs can be resolved without polluting your `Compile` or `Runtime` classpaths.
- Provides a `% Set(...)` syntax for putting a single dependency into multiple configurations at once.
- Generates `webjars.generated.WebJars`, a type-safe Scala locator that knows the group, name, and version of every WebJar you declared, plus URL helpers that automatically choose between a local mount and a CDN.

Requirements
------------

- sbt 1.x (Scala 2.12) or sbt 2.x (Scala 3)
- JDK 17+ at the build's runtime (the generated source targets Scala 3's `enum` syntax)

Install
-------

Add the plugin to `project/plugins.sbt`:

```scala
addSbtPlugin("org.webjars" % "sbt-webjars" % "x.y.z")
```

The plugin is auto-enabled on every JVM project — no `enablePlugins(...)` needed.

Usage
-----

### 1. Declare WebJar dependencies

Use the `% Set(...)` syntax to put a dependency in the `WebJar` configuration along with any other configurations you want it on:

```scala
libraryDependencies ++= Seq(
  "org.webjars.npm" % "bootstrap" % "5.3.8" % Set(WebJar, Compile),
  "org.webjars"     % "jquery"    % "3.7.1" % Set(WebJar, Test),
)
```

The `WebJar` configuration is what the locator generator reads from. Adding the dep to `Compile` (or `Runtime`) bundles the JAR with your app so it can be served locally; adding it only to `Test` keeps it off the production classpath, which makes the `url` helper fall back to a CDN at runtime.

### 2. Use the generated locator

Each compile generates `webjars.generated.WebJars`:

```scala
import webjars.generated.WebJars
import webjars.generated.WebJars.Artifact

// Fully-resolved CDN URL via jsDelivr
WebJars.cdnUrl(Artifact.bootstrap, "css/bootstrap.min.css")
// "https://cdn.jsdelivr.net/webjars/org.webjars.npm/bootstrap/5.3.8/css/bootstrap.min.css"

// Local URL — assumes your app serves WebJars at /webjars/<name>/<version>/<path>
WebJars.localUrl(Artifact.bootstrap, "css/bootstrap.min.css")
// "/webjars/bootstrap/5.3.8/css/bootstrap.min.css"

// Auto-pick: local if the WebJar JAR is on the runtime classpath, CDN otherwise
WebJars.url(Artifact.bootstrap, "css/bootstrap.min.css")
```

`Artifact` is a Scala 3 `enum`, so the compiler will catch typos in WebJar names and you get IDE autocomplete for every artifact you declared.

The `url` helper does a one-time classpath check at the call site for `META-INF/resources/webjars/<name>/<version>/<path>`. The same fact — "is the WebJar JAR bundled?" — that decides whether you registered a `/webjars/` static-asset route also decides which URL to emit, so dev/prod just works without an extra config flag.

### 3. Serve `/webjars/` (optional, for `localUrl` / `url`)

`localUrl` returns paths under `/webjars/`. To serve them, mount any standard WebJars resource handler — for example [webjars-locator-lite](https://github.com/webjars/webjars-locator-lite), the Servlet 3.x default resource handler, or a framework's built-in WebJars support — at `/webjars/*` against the classpath path `META-INF/resources/webjars/`.

Settings
--------

| Key              | Default                | Description                                   |
|------------------|------------------------|-----------------------------------------------|
| `webJarsPackage` | `"webjars.generated"`  | Package for the generated `WebJars` object.   |

Override in `build.sbt` if you want a different package:

```scala
webJarsPackage := "com.example.assets"
```

Tasks
-----

| Task              | Description                                                              |
|-------------------|--------------------------------------------------------------------------|
| `webJarsGenerate` | Generate `WebJars.scala` for the current `WebJar`-scoped dependencies.   |

The generator is wired into `Compile / sourceGenerators`, so a normal `compile` will (re)generate the locator whenever WebJar deps change. You rarely need to invoke `webJarsGenerate` directly.

License
-------

[MIT](https://opensource.org/licenses/MIT)
