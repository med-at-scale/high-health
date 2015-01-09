seq( sbtavro.SbtAvro.avroSettings : _*)

sourceDirectory in avroConfig := file("schemas/ga4gh/src/main/resources/avro/")

libraryDependencies ++= Seq(
                    "org.apache.avro" % "avro" % "1.7.6",
                    "org.apache.avro" % "avro-ipc" % "1.7.6",
                    "org.apache.avro" % "avro-compiler" % "1.7.6"
            )
