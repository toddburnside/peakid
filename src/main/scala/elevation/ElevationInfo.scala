package elevation

import models.GoogleElevationResponse

import scalaz.Reader
import scalaz.concurrent.Task

/**
  * Created by tburnside on 8/22/2016.
  */
object ElevationInfo {
  // TODO: Handle errors - including empty result list
  def getElevation(lon: Double, lat: Double):
    Reader[ElevationProvider, Task[Int]] =
      Reader(_.getElevation(lon, lat).map(_.results(0).elevation.round.toInt))
}
