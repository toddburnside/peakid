package elevation

import cats.implicits._, cats.data._
import fs2.Task

object ElevationInfo {
  // TODO: Handle errors - including empty result list
  def getElevation(lon: Double, lat: Double):
    Reader[ElevationProvider, Task[Throwable Either Int]] =
    Reader(_.getElevation(lon, lat).map {
      task => task.flatMap {
        response => response.results match {
          case h :: t => (h.elevation.round.toInt).asRight // we have data
          case _ => {
            // don't have data, so use the error_message if there is one
            val msg = response.error_message.getOrElse("Unkown Error.")
            (new Exception(msg)).asLeft
          }
        }
      }.leftMap(exc => new Exception("Error getting elevation from Google: " + exc.getMessage) )
    })
}
