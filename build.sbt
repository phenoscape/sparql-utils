import sbt.Keys.{crossScalaVersions, fork, homepage, publishMavenStyle, scalacOptions}

lazy val utestVersion = "0.7.4"

lazy val commonSettings = Seq(
  organization := "org.phenoscape",
  version := "1.2-SNAPSHOT",
  licenses := Seq("MIT license" -> url("https://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/phenoscape/sparql-interpolator")),
  crossScalaVersions := Seq("2.12.11"),
  // Can't support 2.13 until new Contextual release: https://github.com/propensive/contextual/pull/56
  //crossScalaVersions := Seq("2.12.11", "2.13.2"),
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
      <url>git@github.com:phenoscape/sparql-interpolator.git</url>
      <connection>scm:git:git@github.com:phenoscape/sparql-interpolator.git</connection>
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
  scalacOptions in Test ++= Seq("-Yrangepos"),
  fork in Test := true,
  testFrameworks += new TestFramework("utest.runner.Framework")
)

lazy val sparqlInterpolator = project.in(file("."))
  .settings(commonSettings)
  .settings(Seq(skip in publish := true))
  .aggregate(
    core,
    owlapi
  )

lazy val core = project.in(file("modules/core"))
  .settings(commonSettings)
  .settings(testSettings)
  .settings(publishSettings)
  .settings(
    name := "sparql-interpolator",
    description := "Jena SPARQL query string interpolator",
    libraryDependencies ++= Seq(
      "com.propensive" %% "contextual" % "1.2.1",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
      "org.apache.jena" % "apache-jena-libs" % "3.15.0",
      "com.lihaoyi" %% "utest" % utestVersion % Test
    )
  )

lazy val owlapi = project.in(file("modules/owlapi"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(testSettings)
  .settings(publishSettings)
  .settings(
    name := "sparql-interpolator-owlapi",
    description := "SPARQL interpolator OWL API extension",
    libraryDependencies ++= Seq(
      "net.sourceforge.owlapi" % "owlapi-distribution" % "5.1.14",
      "com.lihaoyi" %% "utest" % utestVersion % Test
    )
  )
