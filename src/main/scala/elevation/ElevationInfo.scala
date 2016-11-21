package elevation

import scalaz._
import Scalaz._
import scalaz.concurrent.Task

object ElevationInfo {
  // TODO: Handle errors - including empty result list
  def getElevation(lon: Double, lat: Double):
    Reader[ElevationProvider, Task[Throwable \/ Int]] =
    Reader(_.getElevation(lon, lat).map {
      task => task.flatMap {
        response => response.results match {
          case h :: t => (h.elevation.round.toInt).right // we have data
          case _ => {
            // don't have data, so use the error_message if there is one
            val msg = response.error_message.getOrElse("Unkown Error.")
            (new Exception(msg)).left
          }
        }
      }.leftMap(exc => new Exception("Error getting elevation from Google: " + exc.getMessage) )
    })
}
