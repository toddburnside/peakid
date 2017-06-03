package elevation

import cats.implicits._
import cats.data._
import cats.free.Free
import fs2.Task
import free.freeElevation.{Elevation, Elevations}

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

  def getFreeElevation(lon: Double, lat: Double)(implicit E: Elevations[Elevation]):
    Free[Elevation, Either[Throwable, Int]] = {
//  Reader[ElevationProvider, Task[Throwable Either Int]] = {
//    import E._
    for {
      eitherGoogle <- E.getElevation(lon, lat)
      elevation = eitherGoogle.flatMap {
        response => response.results match {
          case h :: t => (h.elevation.round.toInt).asRight // we have data
          case _ => {
            // don't have data, so use the error_message if there is one
            val msg = response.error_message.getOrElse("Unkown Error.")
            (new Exception(msg)).asLeft
          }
        }
      }.leftMap(exc => new Exception("Error getting elevation from Google: " + exc.getMessage) )
//      googleElevation <- E.getElevation(lon, lat)
//
//      elevation = googleElevation.results.headOption.fold(new Exception(googleElevation.error_message.getOrElse("Unknown Error.")))(_.elevation.round.toInt)
    } yield elevation

  }
}
