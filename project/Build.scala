import sbt._
import Keys._
import play.Project._

// multi modules: https://github.com/kifi/multiproject/blob/master/conf%2Froutes

object HighHealthBuild extends Build {

  val branch = "git rev-parse --abbrev-ref HEAD".!!.trim
  val commit = "git rev-parse --short HEAD".!!.trim
  val buildTime = (new java.text.SimpleDateFormat("yyyyMMdd-HHmmss")).format(new java.util.Date())
  val appVersion = "%s-%s-%s".format(branch, commit, buildTime)

  val hhcommonDependencies = Seq(
    // Add your project dependencies here,
    "med-at-scale"        %  "ga4gh-model-java" % "0.1.0-SNAPSHOT"   excludeAll(ExclusionRule("org.mortbay.jetty"), ExclusionRule("org.eclipse.jetty")),
    "org.apache.avro"     %  "avro-ipc"         % "1.7.6"  excludeAll(ExclusionRule("org.mortbay.jetty"), ExclusionRule("org.eclipse.jetty"))
  )

  //shared by methods
  val hhmethodsDependencies  = Seq(
    "org.apache.spark"    %% "spark-core"       % "1.1.0" ,
    "org.bdgenomics.adam" %  "adam-core"        % "0.15.0"
  )

  // methods
  val hhmetadataDependencies  = Seq()
  val hhreadDependencies      = Seq()
  val hhreferenceDependencies = Seq()
  val hhvariantDependencies   = Seq()
  val hhcustomDependencies    = Seq()

  val allDependencies = hhmethodsDependencies ++ hhmetadataDependencies ++ hhreadDependencies ++ hhreferenceDependencies ++ hhvariantDependencies ++ hhcustomDependencies



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
    version := appVersion
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

  val hhmetadata = play.Project("hhmetadata", appVersion, hhcommonDependencies ++ hhmethodsDependencies ++ hhmetadataDependencies, path = file("methods/hhmetadata")).settings(
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=metadata-application.conf"
  ).dependsOn(hhcommon % "test->test;compile->compile").aggregate(hhcommon)

  val hhread = play.Project("hhread", appVersion, hhcommonDependencies ++ hhmethodsDependencies ++ hhreadDependencies, path = file("methods/hhread")).settings(
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=read-application.conf"
  ).dependsOn(hhcommon % "test->test;compile->compile").aggregate(hhcommon)

  val hhreference = play.Project("hhreference", appVersion, hhcommonDependencies ++ hhmethodsDependencies ++ hhreferenceDependencies, path = file("methods/hhreference")).settings(
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=reference-application.conf"
  ).dependsOn(hhcommon % "test->test;compile->compile").aggregate(hhcommon)

  val hhvariant = play.Project("hhvariant", appVersion, hhcommonDependencies ++ hhmethodsDependencies ++ hhvariantDependencies, path = file("methods/hhvariant")).settings(
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=variant-application.conf"
  ).dependsOn(hhcommon % "test->test;compile->compile").aggregate(hhcommon)

  val hhcustom = play.Project("hhcustom", appVersion, hhcommonDependencies ++ hhmethodsDependencies ++ hhcustomDependencies, path = file("methods/hhcustom")).settings(
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=custom-application.conf"
  ).dependsOn(hhcommon % "test->test;compile->compile").aggregate(hhcommon)


  // The default SBT project is based on the first project alphabetically. To force sbt to use this one,
  // we prefit it with 'aaa'
  val aaaHighHealth = play.Project("high-health", appVersion, hhcommonDependencies ++ allDependencies).settings(
    // This project runs both services together, which is mostly useful in development mode.
    organization := "med-at-scale",
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    resolvers += Resolver.mavenLocal
  )
  .dependsOn( hhcommon    % "test->test;compile->compile",
              hhmetadata  % "test->test;compile->compile",
              hhread      % "test->test;compile->compile",
              hhreference % "test->test;compile->compile",
              hhvariant   % "test->test;compile->compile",
              hhcustom    % "test->test;compile->compile"
            )
  .aggregate( hhcommon,
              hhmetadata,
              hhread,
              hhreference,
              hhvariant,
              hhcustom
            )

}