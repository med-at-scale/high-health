organization := "med-at-scale"

name := "high-health"

version := "0.1.0-SNAPSHOT"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.apache.spark"    %% "spark-core"       % "1.1.0",
  "org.bdgenomics.adam" %  "adam-core"        % "0.15.0",
  "med-at-scale"        %  "ga4gh-model-java" % "0.1.0-SNAPSHOT",
  cache
)

play.Project.playScalaSettings