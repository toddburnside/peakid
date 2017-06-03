name := "Peakid root project"
scalaVersion in ThisBuild := "2.12.1"
scalaOrganization in ThisBuild := "org.typelevel"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
//addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.8.1")

// shared versions
val circeVersion = "0.6.1"

// JVM versions
val http4sVersion = "0.16.0-cats-SNAPSHOT"
val doobieVersion = "0.4.1"
val caseClassyVersion = "0.3.0"
val specs2Version = "3.8.8"
val scalaVersionStr = "2.12.1"

// JS versions
val scalaJSReactVersion = "1.0.0"
val scalaCssVersion = "0.5.3"
val diodeVersion = "1.1.2"
val reactJSVersion = "15.5.4"
val scalaJSDomVersion = "0.9.2" // only needed for AJAX calls

lazy val root = project.in(file("."))
  .aggregate(peakidJS, peakidJVM)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val peakid = crossProject.in(file("."))
  .settings(
    name := "peakid",
    version := "0.1",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion
    )
  )
  .jvmSettings(
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,

      // to enable logging in http4s
      "ch.qos.logback" % "logback-classic" % "1.2.1",

      "org.tpolecat" %% "doobie-core-cats"               % doobieVersion,
      "org.tpolecat" %% "doobie-postgres-cats"           % doobieVersion,
      "org.tpolecat" %% "doobie-hikari-cats"             % doobieVersion,
      "org.tpolecat" %% "doobie-specs2-cats"             % doobieVersion % "test",
      "org.postgis" % "postgis-jdbc" % "1.3.3",

      "com.fortysevendeg" %% "classy-core"            % caseClassyVersion,
      "com.fortysevendeg" %% "classy-config-typesafe" % caseClassyVersion,
      "com.fortysevendeg" %% "classy-generic"         % caseClassyVersion,
      //  "com.fortysevendeg" %% "classy-cats"            % caseClassyVersion,

      "org.specs2" %%% "specs2-core" % specs2Version % "test",
      "org.specs2" %%% "specs2-matcher-extra" % specs2Version % "test",

      "com.github.mpilquist" %% "simulacrum" % "0.10.0"
//      "com.lihaoyi" % "ammonite" % "0.8.2" % "test" cross CrossVersion.full
    )
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true
  )

lazy val peakidJS = peakid.js
lazy val peakidJVM = peakid.jvm

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"     // 2.11 only
)

scalacOptions in Test ++= Seq("-Yrangepos")

//initialCommands in (Test, console) := """ammonite.Main().run()"""
