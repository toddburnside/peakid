name := "Peakid root project"
ThisBuild / scalaVersion := "2.12.4"
//ThisBuild / scalaVersion := "2.12.4-bin-typelevel-4"
//ThisBuild / scalaOrganization  := "org.typelevel"

import sbtcrossproject.{crossProject, CrossType}

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
//addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.8.1")

// shared versions
lazy val circeVersion = "0.9.1"

// JVM versions
lazy val http4sVersion = "0.18.1"
lazy val doobieVersion = "0.5.1"
lazy val catsEffectVersion = "0.9"
lazy val fs2Version = "0.10.2"
lazy val postGisVersion = "1.3.3"
lazy val caseClassyVersion = "0.4.0"
lazy val specs2Version = "4.0.2"
lazy val logbackVersion    = "1.2.3"
lazy val simulacrumVersion = "0.12.0"

// JS versions
lazy val scalaJSReactVersion = "1.1.1"
lazy val scalaCssVersion = "0.5.5"
lazy val diodeVersion = "1.1.3"
lazy val scalaJSDomVersion = "0.9.2" // only needed for AJAX calls
lazy val reactComponentsVersion = "0.8.0"

lazy val reactJSVersion = "15.5.4"
lazy val semanticUIVersion = "2.2.7"
lazy val jqueryVersion = "3.2.1"

lazy val peakid = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
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
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "co.fs2" %% "fs2-core" % fs2Version,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      // to enable logging in http4s
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "org.tpolecat" %% "doobie-specs2" % doobieVersion % "test",
      "org.postgis" % "postgis-jdbc" % postGisVersion,
      "com.47deg" %% "classy-core" % caseClassyVersion,
      "com.47deg" %% "classy-config-typesafe" % caseClassyVersion,
      "com.47deg" %% "classy-generic" % caseClassyVersion,
      //  "com.47deg" %% "classy-cats"            % caseClassyVersion,
      "org.specs2" %%% "specs2-core" % specs2Version % "test",
      "org.specs2" %%% "specs2-matcher-extra" % specs2Version % "test",
      "com.github.mpilquist" %% "simulacrum" % simulacrumVersion
//      "com.lihaoyi" % "ammonite" % "0.8.2" % "test" cross CrossVersion.full
    )
  )
  .jsSettings(
    // The React code has lots of unused parameters
    scalacOptions -= "-Ywarn-unused:params",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReactVersion,
      "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReactVersion,
      "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
      "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion,
      "io.suzaku" %%% "diode" % diodeVersion,
      "io.suzaku" %%% "diode-react" % diodeVersion,
      "org.scala-js" %%% "scalajs-dom" % scalaJSDomVersion // only needed for AJAX calls
//      "com.olvind" %%% "scalajs-react-components" % reactComponentsVersion
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "react" % reactJSVersion / "react-with-addons.js" commonJSName "React" minified "react-with-addons.min.js",
      "org.webjars.npm" % "react-dom" % reactJSVersion / "react-dom.js" commonJSName "ReactDOM" minified "react-dom.min.js" dependsOn "react-with-addons.js",
      "org.webjars" % "jquery" % jqueryVersion / "jquery.js" minified "jquery.min.js",
      "org.webjars" % "Semantic-UI" % semanticUIVersion / "semantic.js" minified "semantic.min.js" dependsOn "jquery.js"
    ),
    skip in packageJSDependencies := false
  )

lazy val peakidJS = peakid.js
//  .enablePlugins(ScalaJSBundlerPlugin)
lazy val peakidJVM = (peakid.jvm)
  .enablePlugins(JavaServerAppPackaging)
  .settings(
    Compile / resourceGenerators += (peakidJS / Compile / fastOptJS)
      .map(f => Seq(f.data)),
    watchSources ++= (peakidJS / watchSources).value
  )

// tpolecat's suggested options
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
  "-Ywarn-unused:params",              // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",            // Warn if a private member is unused.
  "-Ywarn-value-discard",               // Warn when non-Unit expression results are unused.
  // For typelevel compiler
//  "-Yinduction-heuristics",       // speeds up the compilation of inductive implicit resolution
//  "-Ykind-polymorphism",          // type and method definitions with type parameters of arbitrary kinds
//  "-Yliteral-types",              // literals can appear in type position
//  "-Xstrict-patmat-analysis",     // more accurate reporting of failures of match exhaustivity
//  "-Xlint:strict-unsealed-patmat" // warn on inexhaustive matches against unsealed traits
)

Test / scalacOptions ++= Seq("-Yrangepos")
Compile / console / scalacOptions --= Seq("-Ywarn-unused", "-Ywarn-unused-import")

//initialCommands in (Test, console) := """ammonite.Main().run()"""
