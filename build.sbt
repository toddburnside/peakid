scalaVersion := "2.11.8"

val http4sVersion = "0.14.0a-SNAPSHOT"
val circeVersion = "0.4.1"
val doobieVersion = "0.3.0-SNAPSHOT"
val specs2Version = "3.7.1"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.bintrayRepo("oncue", "releases")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-java8" % circeVersion,

  "org.tpolecat" %% "doobie-core"               % doobieVersion,
  "org.tpolecat" %% "doobie-contrib-postgresql" % doobieVersion,
  "org.tpolecat" %% "doobie-contrib-hikari"     % doobieVersion,
  "org.tpolecat" %% "doobie-contrib-specs2"     % doobieVersion,

  "oncue.knobs" %% "core" % "3.6.1a",

  "org.specs2" %% "specs2-core" % specs2Version % "test",
  "org.specs2" %% "specs2-matcher-extra" % specs2Version % "test"

  //"com.lihaoyi" % "ammonite-repl" % "0.5.7" % "test" cross CrossVersion.full
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
  "-Xfuture"
//  "-Ywarn-unused-import"     // 2.11 only
)

scalacOptions in Test ++= Seq("-Yrangepos")

//initialCommands in (Test, console) := """ammonite.repl.Main.run("")"""
