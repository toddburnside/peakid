package services

import io.circe.generic.auto._
import models.GoogleElevationResponse
import org.http4s._
import org.http4s.client.blaze._
import org.http4s.dsl._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task

trait ProfileService extends BaseService {
  def service = HttpService {
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)  =>
//      (lon |@| lat) {Ok(getElevation(_, _))} getOrElse NotFound("x")
//      Ok(s"Received: ($lon, $lat)")
      Ok(getElevation(lon, lat))
  }

  def getElevation(lon: Double, lat: Double): Task[GoogleElevationResponse]
}

case class ProfileServiceGoogle(key: String) extends ProfileService {
  override def getElevation(lon: Double, lat: Double): Task[GoogleElevationResponse] = {
    // TODO: Error handling and get multiple values for peak profile. Also, this won't actually be an endpoint in
    // this fashion. Just for testing the concept.
    val uri = s"https://maps.googleapis.com/maps/api/elevation/json?locations=$lat,$lon&key=$key"
    val client = PooledHttp1Client()
    val response = client.getAs[GoogleElevationResponse](uri)
//    s"Received: ($lon, $lat)"
    response
  }
}
