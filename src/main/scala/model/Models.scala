package common

import java.time.{LocalDate, LocalDateTime}

object Models {

  case class Data(value: Int,
                  trip_class: Int,
                  show_to_affiliates: Boolean,
                  return_date: String,
                  origin: String,
                  number_of_changes: Int,
                  gate: String,
                  found_at: String,
                  duration: Option[Int],
                  distance: Int,
                  destination: String,
                  depart_date: String,
                  actual: Boolean
                 )


  case class IataCityCode(code: String, name: String)

  case class IataResponse(iataCityCode: List[IataCityCode])

  case class Response(success: Boolean, data: List[Data])

}




