scalaVersion := "2.11.8"

val http4sVersion = "0.16.0-cats-SNAPSHOT"
val circeVersion = "0.6.1"
val doobieVersion = "0.4.1"
val caseClassyVersion = "0.3.0"
val specs2Version = "3.8.8"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

//addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.8.1")

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

  // to enable logging in http4s
  "ch.qos.logback" % "logback-classic" % "1.2.1",

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "org.tpolecat" %% "doobie-core-cats"               % doobieVersion,
  "org.tpolecat" %% "doobie-postgres-cats"           % doobieVersion,
  "org.tpolecat" %% "doobie-hikari-cats"             % doobieVersion,
  "org.tpolecat" %% "doobie-specs2-cats"             % doobieVersion % "test",
  "org.postgis" % "postgis-jdbc" % "1.3.3",

  "com.fortysevendeg" %% "classy-core"            % caseClassyVersion,
  "com.fortysevendeg" %% "classy-config-typesafe" % caseClassyVersion,
  "com.fortysevendeg" %% "classy-generic"         % caseClassyVersion,
//  "com.fortysevendeg" %% "classy-cats"            % caseClassyVersion,

  "org.specs2" %% "specs2-core" % specs2Version % "test",
  "org.specs2" %% "specs2-matcher-extra" % specs2Version % "test",

  "com.github.mpilquist" %% "simulacrum" % "0.10.0",
  "com.lihaoyi" % "ammonite" % "0.8.2" % "test" cross CrossVersion.full

)

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

initialCommands in (Test, console) := """ammonite.Main().run()"""
