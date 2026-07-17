sys.props.get("plugin.version") match {
  case Some(v) => addSbtPlugin("org.webjars" % "sbt-webjars" % v)
  case _       => sys.error("'plugin.version' not set")
}
