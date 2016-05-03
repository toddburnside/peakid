scalaVersion := "2.11.8"

val http4sVersion = "0.13.2"

val circeVersion = "0.4.1"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-java8" % circeVersion

  //"com.lihaoyi" % "ammonite-repl" % "0.5.7" % "test" cross CrossVersion.full
)

initialCommands in (Test, console) := """ammonite.repl.Main.run("")"""
