package services

import org.http4s._
import org.http4s.dsl._
import scalaz._, Scalaz._
import services.QueryParams.{LatMatcher, LonMatcher}

object DoubleVar {
  def unapply(str: String): Option[Double] = {
    if (!str.isEmpty && str.matches("^\\d+(\\.\\d+)?$"))
      try {
        Some(str.toDouble)
      } catch {
        case _: NumberFormatException =>
          None
      }
    else
      None
  }
}


trait ProfileService {
  def service = HttpService {
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)  =>
//      (lon |@| lat) {Ok(getElevation(_, _))} getOrElse NotFound("x")
      Ok(s"Received: ($lon, $lat)")
//      Ok(getElevation(lon, lat))
  }

  def getElevation(lon: Double, lat: Double): String
}

case class ProfileServiceGoogle(x: Int) extends ProfileService {
  override def getElevation(lon: Double, lat: Double): String = {
    s"Received: ($lon, $lat)"
  }
}
