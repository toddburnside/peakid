package free

import cats.free.{Free, Inject}
import cats.~>
import fs2.Task
import elevation. ElevationProvider
import models.GoogleElevationResponse

object freeElevation {

  sealed trait Elevation[A]
  case class GetElevation(lon: Double, lat: Double) extends Elevation[Either[Throwable, GoogleElevationResponse]]

  class Elevations[F[_]](implicit I: Inject[Elevation, F]) {
    def getElevation(lon: Double, lat: Double): Free[F, Either[Throwable, GoogleElevationResponse]] =
      Free.inject[Elevation, F](GetElevation(lon, lat))
  }

  object Elevations {
    implicit def elevations[F[_]](implicit I: Inject[Elevation, F]): Elevations[F] = new Elevations[F]
  }

  // TODO: Should interpret to lower level?
  def googleElevationInterpreter(elevProv: ElevationProvider): Elevation ~> Task =
    new (Elevation ~> Task) {
      override def apply[A](fa: Elevation[A]): Task[A] = fa match {
        case GetElevation(lon, lat) => elevProv.getElevation(lon, lat)
      }
    }
}