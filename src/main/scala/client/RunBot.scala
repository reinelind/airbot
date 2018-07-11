package client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.io.StdIn

object RunBot extends App {
  implicit val system = ActorSystem ("bot")
  implicit val materializer = ActorMaterializer()
  val client = new Client
  val bot = AviaBot(client ,"528618622:AAERcLVWG5eA29h1WEwR-Fr4I1bGHHrwESA")
  StdIn.readLine()
}
