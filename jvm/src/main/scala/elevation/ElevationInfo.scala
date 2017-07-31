package elevation

import cats.data._
import fs2.Task

object ElevationInfo {
  // TODO: Handle errors - including empty result list
  def getElevation(
      lon: Double,
      lat: Double): Reader[ElevationProvider, Task[Throwable Either Int]] =
    Reader(_.getElevation(lon, lat))
}
