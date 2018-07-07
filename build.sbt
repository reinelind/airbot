name := "akkahello"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= {
  val AkkaHttpVersion   = "10.1.3"
  val AkkaStreamVersion = "2.5.12"
  Seq(
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "com.typesafe.akka" %% "akka-stream"     % AkkaStreamVersion,
    "com.typesafe.akka" %% "akka-http"       % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",
    "info.mukel" %% "telegrambot4s" % "3.0.15",
    "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
  )
}