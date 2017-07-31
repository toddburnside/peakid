package repositories

import models.PeakBase.{NewPeak, Peak}
import models.{Location3D, VisiblePeak}

import fs2.{Task, Stream}
import cats.data._

object PeakInfo {
  def find(minElev: Int): Reader[PeakRepository, Stream[Task, Peak]] =
    Reader(_.find(minElev))

  def findOne(
      id: Int): Reader[PeakRepository, Task[Throwable Either Option[Peak]]] =
    Reader(_.findOne(id))

  def insert(
      newPeak: NewPeak): Reader[PeakRepository, Task[Throwable Either Peak]] =
    Reader(_.insert(newPeak))

  def findVisible(minElev: Int, loc: Location3D)
    : Reader[PeakRepository, Task[Throwable Either Vector[VisiblePeak]]] = {

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
      process <- find(minElev)
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
        .runLog
        .attempt // could just let http4s handle the exception
  }
}
