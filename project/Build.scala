import sbt._
import Keys._
import play.PlayImport._

// multi modules: https://github.com/kifi/multiproject/blob/master/conf%2Froutes

object HighHealthBuild extends Build {

  val branch = "git rev-parse --abbrev-ref HEAD".!!.trim
  val commit = "git rev-parse --short HEAD".!!.trim
  val buildTime = (new java.text.SimpleDateFormat("yyyyMMdd-HHmmss")).format(new java.util.Date())
  val appVersion = "%s-%s-%s".format(branch, commit, buildTime)

  val hhcommonDependencies = Seq(
    // Add your project dependencies here,
    "med-at-scale"        %  "ga4gh-model-java" % "0.1.0-SNAPSHOT"   excludeAll(ExclusionRule("org.mortbay.jetty"), ExclusionRule("org.eclipse.jetty")),
    "org.apache.avro"     %  "avro-ipc"         % "1.7.6"  excludeAll(ExclusionRule("org.mortbay.jetty"), ExclusionRule("org.eclipse.jetty")),
    "com.typesafe.play"   %% "play"             % "2.3.7"  excludeAll(ExclusionRule("com.typesafe.akka"))
    // can be a better option for CORS → "com.github.dwhjames" %% "play-cors"        % "0.1.0"  excludeAll(ExclusionRule("com.typesafe.akka"))
  )

  //shared by methods
  val hhmethodsDependencies  = Seq(
    "org.apache.spark"    %% "spark-core"       % "1.2.1" ,
    "org.bdgenomics.adam" %  "adam-core"        % "0.16.0"
  )

  // methods
  val hhmetadataDependencies  = Seq()
  val hhreadDependencies      = Seq()
  val hhreferenceDependencies = Seq()
  val hhvariantDependencies   = Seq()
  val hhcustomDependencies    = Seq()
  val hhbeaconDependencies    = Seq()

  val allDependencies = hhmethodsDependencies ++ hhmetadataDependencies ++ hhreadDependencies ++ hhreferenceDependencies ++ hhvariantDependencies ++ hhbeaconDependencies ++ hhcustomDependencies

  val allResolvers = Seq(
    Resolver.mavenLocal,
    bintray.Opts.resolver.repo("dwhjames", "maven"), //cors
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  val scalaBuildOptions = Seq("-unchecked",
                              "-deprecation",
                              "-feature",
                              "-language:reflectiveCalls",
                              "-language:implicitConversions",
                              "-language:postfixOps",
                              "-language:dynamics",
                              "-language:higherKinds",
                              "-language:existentials",
                              "-language:experimental.macros",
                              "-Xmax-classfile-name",
                              "140")

  val hhcommon = Project("hhcommon", file("hhcommon"))
  .settings(
    resolvers ++= allResolvers
  )
  .settings(
    libraryDependencies ++= hhcommonDependencies ++ hhmethodsDependencies,
    libraryDependencies ++= Seq(
        "com.typesafe" % "config" % "1.2.1"
    )
  )
  .settings(
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=common-application.conf"
  )

  val hhmetadata = Project("hhmetadata", file("methods/hhmetadata")).enablePlugins(play.PlayScala).settings(
      libraryDependencies ++= hhcommonDependencies ++ hhmethodsDependencies ++ hhmetadataDependencies,
      scalacOptions ++= scalaBuildOptions,
      sources in doc in Compile := List(),
      javaOptions in Test += "-Dconfig.resource=metadata-application.conf"
    )
    .dependsOn(hhcommon % "test->test;compile->compile")
    .aggregate(hhcommon)

  val hhread = Project("hhread", file("methods/hhread")).enablePlugins(play.PlayScala).settings(
      libraryDependencies ++= hhcommonDependencies ++ hhmethodsDependencies ++ hhreadDependencies,
      scalacOptions ++= scalaBuildOptions,
      sources in doc in Compile := List(),
      javaOptions in Test += "-Dconfig.resource=read-application.conf"
    )
    .dependsOn(hhcommon % "test->test;compile->compile")
    .aggregate(hhcommon)

  val hhreference = Project("hhreference", file("methods/hhreference")).enablePlugins(play.PlayScala).settings(
      libraryDependencies ++= hhcommonDependencies ++ hhmethodsDependencies ++ hhreferenceDependencies,
      scalacOptions ++= scalaBuildOptions,
      sources in doc in Compile := List(),
      javaOptions in Test += "-Dconfig.resource=reference-application.conf"
    )
    .dependsOn(hhcommon % "test->test;compile->compile")
    .aggregate(hhcommon)

  val hhvariant = Project("hhvariant", file("methods/hhvariant")).enablePlugins(play.PlayScala).settings(
      libraryDependencies ++= hhcommonDependencies ++ hhmethodsDependencies ++ hhvariantDependencies,
      scalacOptions ++= scalaBuildOptions,
      sources in doc in Compile := List(),
      javaOptions in Test += "-Dconfig.resource=variant-application.conf"
    )
    .dependsOn(hhcommon % "test->test;compile->compile")
    .aggregate(hhcommon)

  val hhbeacon = Project("hhbeacon", file("methods/hhbeacon")).enablePlugins(play.PlayScala).settings(
      libraryDependencies ++= hhcommonDependencies ++ hhmethodsDependencies ++ hhbeaconDependencies,
      scalacOptions ++= scalaBuildOptions,
      sources in doc in Compile := List(),
      javaOptions in Test += "-Dconfig.resource=beacon-application.conf"
    )
    .dependsOn(hhcommon % "test->test;compile->compile")
    .aggregate(hhcommon)

  val hhcustom = Project("hhcustom", file("methods/hhcustom")).enablePlugins(play.PlayScala).settings(
      libraryDependencies ++= hhcommonDependencies ++ hhmethodsDependencies ++ hhcustomDependencies,
      scalacOptions ++= scalaBuildOptions,
      sources in doc in Compile := List(),
      javaOptions in Test += "-Dconfig.resource=custom-application.conf"
    )
    .dependsOn(hhcommon % "test->test;compile->compile")
    .aggregate(hhcommon)


  // The default SBT project is based on the first project alphabetically. To force sbt to use this one,
  // we prefit it with 'aaa'
  val aaaHighHealth = Project("high-health", file(".")).enablePlugins(play.PlayScala).settings(
    // This project runs both services together, which is mostly useful in development mode.
    version := appVersion,
    organization := "med-at-scale",
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    resolvers ++= allResolvers,
    libraryDependencies ++= hhcommonDependencies ++ allDependencies,
    libraryDependencies +=  filters
  )
  .dependsOn( hhcommon    % "test->test;compile->compile",
              hhmetadata  % "test->test;compile->compile",
              hhread      % "test->test;compile->compile",
              hhreference % "test->test;compile->compile",
              hhvariant   % "test->test;compile->compile",
              hhbeacon    % "test->test;compile->compile",
              hhcustom    % "test->test;compile->compile"
            )
  .aggregate( hhcommon,
              hhmetadata,
              hhread,
              hhreference,
              hhvariant,
              hhbeacon,
              hhcustom
            )
}