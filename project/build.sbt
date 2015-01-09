resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)

resolvers += "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases"

addSbtPlugin("com.cavorite" % "sbt-avro" % "0.3.2-mas")
