package services

import org.http4s._
import org.http4s.dsl._

object PeakService {
  val service = HttpService {
    case GET -> Root / "peaks" => Ok("The Peaks have it")
  }
}
