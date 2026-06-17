package example

// Imports the generated locator to force compile to fail if the
// generated source isn't on the source path.
import webjars.generated.WebJars
import webjars.generated.WebJars.Artifact

object Main:
  def main(args: Array[String]): Unit =
    val _ = WebJars.cdnUrl(Artifact.bootstrap, "dist/css/bootstrap.min.css")
    println("OK")
