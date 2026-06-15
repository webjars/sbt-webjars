package example

import webjars.generated.WebJars
import webjars.generated.WebJars.Artifact

/** Runtime smoke test for the generated WebJars locator.
  *
  * `bootstrap` is declared as `Set(WebJar, Compile)`, so its JAR is on the
  * runtime classpath and `WebJars.url` should pick `localUrl`.
  *
  * `jquery` is declared as `Set(WebJar, Test)`, so its JAR is *not* on the
  * `runMain` classpath and `WebJars.url` should fall back to `cdnUrl`.
  *
  * Resource paths are chosen to match what the NPM/classic WebJars actually
  * ship inside `META-INF/resources/webjars/<name>/<version>/`:
  *   - bootstrap (NPM): `dist/css/bootstrap.min.css`
  *   - jquery   (classic): `jquery.min.js`
  */
object Main:
  def main(args: Array[String]): Unit =
    val bootstrapPath = "dist/css/bootstrap.min.css"
    val jqueryPath    = "jquery.min.js"

    val cdnExpected =
      s"https://cdn.jsdelivr.net/webjars/org.webjars.npm/bootstrap/5.3.8/$bootstrapPath"
    val cdn = WebJars.cdnUrl(Artifact.bootstrap, bootstrapPath)
    assert(cdn == cdnExpected, s"cdnUrl mismatch: got $cdn, want $cdnExpected")

    val localExpected = s"/webjars/bootstrap/5.3.8/$bootstrapPath"
    val local         = WebJars.localUrl(Artifact.bootstrap, bootstrapPath)
    assert(local == localExpected, s"localUrl mismatch: got $local, want $localExpected")

    val resolved = WebJars.url(Artifact.bootstrap, bootstrapPath)
    assert(resolved == localExpected,
      s"url should pick localUrl for a bundled WebJar: got $resolved")

    val jqExpected =
      s"https://cdn.jsdelivr.net/webjars/org.webjars/jquery/3.7.1/$jqueryPath"
    val jq = WebJars.url(Artifact.jquery, jqueryPath)
    assert(jq == jqExpected,
      s"url should fall back to CDN for a non-bundled WebJar: got $jq")

    println("OK")
