package elevation

import io.circe.Decoder
import io.circe.generic.auto._
import models.GoogleElevationResponse
import org.http4s.client.Client

import fs2.Task
import cats.implicits._

trait ElevationProvider {
  def getElevation(lon: Double, lat: Double): Task[Throwable Either Int]
}

class GoogleElevationProvider(key: String, client: Client)
    extends ElevationProvider {
  // TODO: This is also in BaseService - maybe it should be moved elsewhere.
  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) =
    org.http4s.circe.jsonOf[A]

  override def getElevation(lon: Double,
                            lat: Double): Task[Throwable Either Int] = {
    val uri =
      s"https://maps.googleapis.com/maps/api/elevation/json?locations=$lat,$lon&key=$key"
    val responseET = client.expect[GoogleElevationResponse](uri).attempt
    // might be a better way than map + flatMap...
    responseET
      .map { responseE => // get the Either from within the Task
        responseE
          .flatMap { r => // The response from Google (assuming no error)
            // now, see if we have data in the response
            r.results match {
              // we only asked for one elevation, so all we care about is the first element
              case h :: t => (h.elevation.round.toInt).asRight
              case _ => {
                // don't have data, so use the error_message if there is one
                val msg = r.error_message.getOrElse("Unkown Error.")
                (new Exception(msg)).asLeft
              }
            }
          }
          .leftMap(e =>
            // e could be from an exception in client or deserialization, or from an error in the response data
            new Exception(
              s"Error getting elevation from Google: ${e.getMessage}"))
      }
  }
}
