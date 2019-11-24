sonatypeProfileName := "io.razem"

isSnapshot := version.value.contains("SNAPSHOT")

publishMavenStyle := true

licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(
  GitHubHosting("razem-io", "scala-influxdb-client", "scala-influxdb-client@pieles.digital")
)
