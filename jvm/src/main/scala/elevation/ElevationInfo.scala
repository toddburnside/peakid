package elevation

import cats.data._

object ElevationInfo {
  // TODO: Handle errors - including empty result list
  def getElevation[F[_]](
      lon: Double,
      lat: Double): Kleisli[F, ElevationProvider[F], Throwable Either Int] =
    Kleisli(_.getElevation(lon, lat))
}
