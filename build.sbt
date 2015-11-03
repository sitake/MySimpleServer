name := "Server"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-httpx" % "6.29.0",
  "org.json4s" %% "json4s-jackson" % "3.3.0"
)