
organization  := "org.phenoscape"

name          := "sparql-interpolator"

version       := "1.0"

publishMavenStyle := true

publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
    else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

licenses := Seq("MIT license" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/phenoscape/sparql-interpolator"))

crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.3")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

fork in Test := true

libraryDependencies ++= {
  Seq(
    "com.propensive"  %% "contextual"       % "1.0.1",
    "org.apache.jena" %  "apache-jena-libs" % "3.2.0" pomOnly()
  )
}

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
