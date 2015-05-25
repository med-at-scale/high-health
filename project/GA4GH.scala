import sbt._

object GA4GH {

  val defaultGA4GHVersion = sys.props.getOrElse("ga4gh.version", "0.5.1")
  val defaultGA4GHJavaVersion = "0.1.0-SNAPSHOT"

  val ga4ghJavaVersionMap = Map(
    "0.5.1" -> "0.1.0-SNAPSHOT",
    "0.6.0" -> "0.1.1-SNAPSHOT"
  )

  val ga4ghJavaVersion = ga4ghJavaVersionMap.getOrElse(defaultGA4GHVersion, defaultGA4GHJavaVersion)

  def ga4ghDir(dir: String) = s"${dir}/ga4gh_${defaultGA4GHVersion}"
  def ga4ghMethodDir(method: String) = s"methods/ga4gh_${defaultGA4GHVersion}/${method}"

  val ga4ghDependency = "med-at-scale" %  "ga4gh-model-java" % ga4ghJavaVersion excludeAll(ExclusionRule("org.mortbay.jetty"), ExclusionRule("org.eclipse.jetty"))
}