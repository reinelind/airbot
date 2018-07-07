package common

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json.DefaultJsonProtocol
import spray.json._
import Models._

trait JsonParser extends DefaultJsonProtocol with SprayJsonSupport {

implicit val iataCityCode = jsonFormat2(IataCityCode)

  implicit object IataResponseFormat extends RootJsonFormat[IataResponse] {
    override def write(obj: IataResponse): JsValue = JsObject(
      "response" -> JsArray(obj.iataCityCode.map(_.toJson).toVector)
    )

    override def read(json: JsValue): IataResponse = {
      json.asJsObject.getFields("response") match {
        case Seq(JsArray(dat)) =>
          IataResponse(dat.map(_.convertTo[IataCityCode]).toList)
        case _ => throw DeserializationException("IataResponse expected")
      }
    }
  }

  implicit object DataJsonFormat extends RootJsonFormat[Data] {
    override def write(obj: Data): JsValue = JsObject(
      "value" -> Option(obj.value).map(JsNumber(_)).getOrElse(JsNull),
      "trip_class" -> Option(obj.trip_class).map(JsNumber(_)).getOrElse(JsNull),
      "show_to_affiliates" -> JsBoolean(obj.show_to_affiliates),
      "return_date" -> JsString(obj.return_date),
      "origin" -> JsString(obj.origin),
      "number_of_changes" -> Option(obj.number_of_changes).map(JsNumber(_)).getOrElse(JsNull),
      "gate" -> JsString(obj.gate),
      "found_at" -> JsString(obj.found_at),
      "duration" -> JsNumber(obj.duration.getOrElse(0)),
      "distance" -> Option(obj.distance).map(JsNumber(_)).getOrElse(JsNull),
      "destination" -> JsString(obj.destination),
      "depart_date" -> JsString(obj.depart_date),
      "actual" -> JsBoolean(obj.actual)
    )

    override def read(json: JsValue): Data = json match {
      case JsObject(fields) =>
        val value: Int = fields.get("value").map(_.convertTo[Int]).getOrElse(0)
        val trip_class: Int = fields.get("trip_class").map(_.convertTo[Int]).getOrElse(0)
        val number_of_changes: Int = fields.get("number_of_changes").map(_.convertTo[Int]).getOrElse(0)
        val duration: Option[Int] = fields.get("duration") match {
          case Some(JsNull) =>  None
          case _ => Some (fields.get("duration").map(_.convertTo[Int]).getOrElse(0))
        }
        val distance: Int = fields.get("distance").map(_.convertTo[Int]).getOrElse(0)
        json.asJsObject.getFields("show_to_affiliates", "return_date", "origin", "gate", "found_at", "destination", "depart_date", "actual") match {
          case Seq(JsBoolean(sta), JsString(rd), JsString(origin), JsString(gate), JsString(foundAt), JsString(dest), JsString(depart), JsBoolean(actual)) =>
            Data(value, trip_class, sta, rd, origin, number_of_changes, gate, foundAt, duration, distance, dest, depart, actual)
          case _ => throw new DeserializationException("Data expected")
        }
    }
  }

  implicit val data = jsonFormat13(Data)

  implicit object modelFormat extends RootJsonFormat[Response] {
    def write(r: Response): JsValue = JsObject(
      "success" -> JsBoolean(r.success),
      "data" -> JsArray(r.data.map(_.toJson).toVector)
    )

    def read(value: JsValue): Response = {
      value.asJsObject.getFields("success", "data") match {
        case Seq(JsBoolean(suc), JsArray(dat)) =>
          new Response(suc, dat.map(_.convertTo[Data]).toList)
        case _ => throw new DeserializationException("Response expected")
      }
    }
  }
}
