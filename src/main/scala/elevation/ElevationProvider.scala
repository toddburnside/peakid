package elevation

import io.circe.Decoder
import io.circe.generic.auto._
import models.GoogleElevationResponse
import org.http4s.client.Client

import fs2.Task

trait ElevationProvider {
  def getElevation(lon: Double, lat: Double): Task[Throwable Either GoogleElevationResponse]
}

class GoogleElevationProvider(key: String, client: Client) extends ElevationProvider {
  // TODO: This is also in BaseService - maybe it should be moved elsewhere.
  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) = org.http4s.circe.jsonOf[A]

  override def getElevation(lon: Double, lat: Double): Task[Throwable Either GoogleElevationResponse] = {
    val uri = s"https://maps.googleapis.com/maps/api/elevation/json?locations=$lat,$lon&key=$key"
    client.expect[GoogleElevationResponse](uri).attempt
  }

//  override def getFreeElevation(lon: Double, lat: Double): Task[Throwable Either GoogleElevationResponse] = {
//    val uri = s"https://maps.googleapis.com/maps/api/elevation/json?locations=$lat,$lon&key=$key"
//    client.expect[GoogleElevationResponse](uri).attempt
//  }
}