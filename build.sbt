import sbt.Keys.{crossScalaVersions, fork, homepage, publishMavenStyle, scalacOptions}

lazy val utestVersion = "0.7.7"

lazy val commonSettings = Seq(
  organization := "org.phenoscape",
  version := "1.3.1",
  licenses := Seq("MIT license" -> url("https://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/phenoscape/sparql-utils")),
  crossScalaVersions := Seq("2.12.13", "2.13.5"),
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)

lazy val publishSettings = Seq(
  publishArtifact in Test := false,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <scm>
      <url>git@github.com:phenoscape/sparql-utils.git</url>
      <connection>scm:git:git@github.com:phenoscape/sparql-utils.git</connection>
    </scm>
      <developers>
        <developer>
          <id>balhoff</id>
          <name>Jim Balhoff</name>
          <email>balhoff@renci.org</email>
        </developer>
      </developers>
    )
)

lazy val testSettings = Seq(
  scalacOptions in Test ++= Seq("-Yrangepos", "-feature"),
  fork in Test := true,
  testFrameworks += new TestFramework("utest.runner.Framework"),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "utest" % utestVersion % Test
  )
)

lazy val parentProject = project.in(file("."))
  .settings(commonSettings)
  .settings(
    name := "sparql-utils-project",
    skip in publish := true)
  .aggregate(
    core,
    owlapi
  )

lazy val core = project.in(file("modules/core"))
  .settings(commonSettings)
  .settings(testSettings)
  .settings(publishSettings)
  .settings(
    name := "sparql-utils",
    description := "Jena SPARQL utilities for Scala",
    libraryDependencies ++= Seq(
      "com.propensive" %% "contextual-core" % "3.0.0",
      "com.propensive" %% "magnolia"        % "0.17.0",
      "org.scala-lang"  % "scala-reflect"   % scalaVersion.value % Provided,
      "org.apache.jena" % "jena-arq"        % "4.1.0"
    )
  )

lazy val owlapi = project.in(file("modules/owlapi"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(testSettings)
  .settings(publishSettings)
  .settings(
    name := "sparql-utils-owlapi",
    description := "SPARQL utilities OWL API extension",
    libraryDependencies ++= Seq(
      "net.sourceforge.owlapi" % "owlapi-distribution" % "4.5.16"
    )
  )
