scalaVersion := "2.11.8"

lazy val http4sVersion = "0.13.2"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "com.lihaoyi" % "ammonite-repl" % "0.5.7" % "test" cross CrossVersion.full
)

initialCommands in (Test, console) := """ammonite.repl.Main.run("")"""
