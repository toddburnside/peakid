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
    : Reader[PeakRepository, Task[Throwable Either Vector[VisiblePeak]]] =
    // get a list of peaks from the server as a process, then filter them, then put into a Vector,
    // then handle exceptions by 'attempt'ing them into a disjunction.
    for {
      process <- find(minElev)
    } yield
      process
        .filter(_.isVisibleFrom(loc))
        .map { peak =>
          val dist = loc.distanceTo(peak)
          val bearing = loc.bearingTo(peak)
          VisiblePeak(peak.id,
                      peak.name,
                      peak.elevation,
                      bearing,
                      dist,
                      peak.location)
        }
        .runLog
        .attempt
}
