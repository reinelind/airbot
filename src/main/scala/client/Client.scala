package client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.{Await, Future}
import concurrent.ExecutionContext.Implicits.global
import common._
import common.Models._


import scala.util.{Failure, Success}
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative.Commands

import scala.concurrent.duration.Duration


class Client extends JsonParser {

  def toIataCode (from : String, to : String) : (String, String) ={

    implicit val system = ActorSystem("iataCode")
    implicit val materializer = ActorMaterializer()

    val URL = "http://iatacodes.org/api/v6/cities?api_key=40c3b809-3633-416e-a325-d7b030c7e9b8&city="

    val iataResponseFuture = Http().singleRequest(HttpRequest(uri = URL))
      .flatMap(response => Unmarshal(response).to[IataResponse])

    val iataResponse = Await.result(iataResponseFuture, Duration.Inf )

    val departure = iataResponse.iataCityCode.filter(city => city
      .name.toLowerCase == from.toLowerCase().replace(" ", ""))
    val arrival = iataResponse.iataCityCode.filter(city => city
        .name.toLowerCase == to.toLowerCase().replace(" ", ""))



    (if (departure.isEmpty) "" else departure.head.code,
      if (arrival.isEmpty) "" else arrival.head.code)
  }

  def Run(from: String, to: String) : Response = {

    val origin = if (from.isEmpty)
      ""
    else
      "&origin=" ++ from

    val destination = if (to.isEmpty)
      ""
    else
      "&destination=" ++ to

    implicit val actorSystem = ActorSystem("client")
    implicit val actorMaterializer = ActorMaterializer()

    val httpClient = Http().outgoingConnection("api.travelpayouts.com")

    val URL = s"""/v2/prices/latest?currency=uah$origin$destination&limit=14&show_to_affiliates=true&sorting=price&trip_duration=1&token=ac7e3fcb7f74c206721a29fbca94ff83"""

    val flowGet : Future[Response] =
      Source.single(HttpRequest(HttpMethods.GET,Uri(URL)))
        .via(httpClient)
        .mapAsync(1)(response => Unmarshal (response.entity)
          .to[Response])
        .runWith(Sink.head)
        .andThen{
          case Success (_) => println ("Request succeeded")
          case Failure (_) => println ("Request failure")
        }

    flowGet.andThen{
      case _ => actorSystem.terminate()
        println ("actorSystem terminated")
    }

    val result = Await.result (flowGet, Duration.Inf)

    result
  }


}