package repositories

import models.PeakBase.{NewPeak, Peak}
import models.{Location3D, VisiblePeak}
import fs2.Stream
import cats.data._
import cats.implicits._
import cats.effect.Effect

object PeakInfo {
  def find[F[_]](minElev: Int): Reader[PeakRepository[F], Stream[F, Peak]] =
    Reader(_.find(minElev))

  def findOne[F[_]](
      id: Int): Kleisli[F, PeakRepository[F], Throwable Either Option[Peak]] =
    Kleisli(_.findOne(id))

  def insert[F[_]](
      newPeak: NewPeak): Kleisli[F, PeakRepository[F], Throwable Either Peak] =
    Kleisli(_.insert(newPeak))

  def findVisible[F[_]: Effect](minElev: Int, loc: Location3D)
    : Reader[PeakRepository[F], F[Throwable Either Vector[VisiblePeak]]] = {

    // TODO: Handle units more consistently, these conversions DO NOT belong here
    // Problems are: db has elevation in feet, but need meters for calculations and
    // need to return miles instead of km. Should allow user to specify units, either
    // in the server, or the front-end.
    val kmToMiles = (d: Double) => d * 0.621371
    val elevFeetToMeters = (p: Peak) =>
      p.copy(elevation = (p.elevation * 0.3048).round.toInt)

    // get a list of peaks from the server as a process, then filter them, then put into a Vector,
    // then handle exceptions by 'attempt'ing them into a disjunction.
    for {
      process <- find[F](minElev)
    } yield
      process
        .filter(elevFeetToMeters(_).isVisibleFrom(loc))
        .map { peak =>
          val dist = kmToMiles(loc.distanceTo(peak))
          val bearing = loc.bearingTo(peak)
          VisiblePeak(peak.id,
                      peak.name,
                      peak.elevation,
                      bearing,
                      dist,
                      peak.location)
        }
        .compile
        .toVector
        .attempt // could just let http4s handle the exception
  }
}
