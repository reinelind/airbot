package client

import info.mukel.telegrambot4s._
import api._
import common.Models._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative.{Commands, InlineQueries}


abstract class AbstractBot (val token: String) extends TelegramBot

class AviaBot (client : Client, token : String)  extends AbstractBot(token) with Polling with Commands {
  onCommand("/start") { implicit msg =>
    reply(
      """Greetings! This is a bot for searching available air tickets
        |Use /ticket to get available tickets. Enter departure and destination cities
        |Use /allticketsTo to get all available tickets to place of arrival
        |Use /allticketsFrom to get all available tickets from place of departure""".stripMargin)
    using (_.from){
      user =>
        println (s"${user.firstName} ${user.lastName.get} connected ")
    }
  }


  onCommand("/ticket") { implicit msg =>
    withArgs { args =>
      reply(if (args.isEmpty || !(args.size == 2))
        "Enter two codes!"
      else  {

        val code =client.toIataCode(args.head, args.last)
        val tickets = client.Run(code._1, code._2)
        def ticketOutput(ticket: List[Data]): String = {
          ticket match {
            case x :: xs => ticketSimpleFormatOutput(x) ++ ticketOutput(xs)
            case Nil => ""
          }
        }
        ticketOutput(tickets.data)
      })

    }

    using(_.from) {
      user =>
        println(s"command /ticket called from user ${user.firstName} ${user.lastName.get}")

    }
  }


  onCommand ("/allticketsto") {implicit msg =>
    withArgs { args =>
    reply (if (args.isEmpty || !(args.size == 1))
      "Enter only one code"
    else  {

      val code =client.toIataCode("",args.head)
      val tickets = client.Run("", code._2)
      def ticketOutput (ticket : List[Data]) : String = {
        ticket match {
          case x :: xs => ticketSimpleFormatOutput(x)  ++ ticketOutput(xs)
          case Nil => ""
        }
      }

      ticketOutput(tickets.data)
    })

  }
}

  onCommand ("/allticketsfrom") {implicit msg =>
    withArgs { args =>
      reply (if (args.isEmpty || !(args.size == 1))
        "Enter only one code"
      else  {

        val code =client.toIataCode(args.head,"")
        val tickets = client.Run(code._1 , "")
        def ticketOutput (ticket : List[Data]) : String = {
          ticket match {
            case x :: xs => ticketSimpleFormatOutput(x)  ++ ticketOutput(xs)
            case Nil => ""
          }
        }
      ticketOutput(tickets.data)
      })

    }
  }


  def ticketSimpleFormatOutput (ticket : Data) : String = {
    s"""\n
       |Cost                      : ${ticket.value} UAH
       |Departure           : ${ticket.origin}
       |Destination        : ${ticket.destination}
       |Departure date : ${ticket.depart_date} UTC
       |Arrival date         : ${ticket.return_date} UTC
       |Trip class              : ${ticket.trip_class match {
      case 0 => "Economy class"
      case 1 => "Business class"
      case 2 => "First class"
    }}
       |Available:             : ${if (ticket.actual) "Actual" else "Not actual"}""".stripMargin
  }
}

object AviaBot {
  def apply (client : Client ,tok : String) = new AviaBot(client, tok).run()
}


