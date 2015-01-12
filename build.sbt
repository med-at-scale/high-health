name := "high-health"

version := "0.1.0-SNAPSHOT"

seq( sbtavro.SbtAvro.avroSettings : _*)

sourceDirectory in avroConfig := file("schemas/ga4gh/src/main/resources/avro/")

libraryDependencies ++= Seq(
  //jdbc,
  //anorm,
  cache
)

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.7.6",
  "org.apache.avro" % "avro-ipc" % "1.7.6",
  "org.apache.avro" % "avro-compiler" % "1.7.6"
)

play.Project.playScalaSettings
