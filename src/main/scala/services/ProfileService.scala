package services

import elevation.{ElevationInfo, ElevationProvider}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task

class ProfileService(val elevProvider: ElevationProvider) extends BaseService {
  def service = HttpService {
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)  =>
//      (lon |@| lat) {Ok(getElevation(_, _))} getOrElse NotFound("x")
//      Ok(s"Received: ($lon, $lat)")
      Ok(ElevationInfo.getElevation(lon, lat).run(elevProvider))
  }
}
