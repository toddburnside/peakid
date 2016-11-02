package repositories

//import cats.data.Reader
import models.PeakBase.{NewPeak, Peak}
import models.{Location3D, VisiblePeak}

import scalaz._
import scalaz.concurrent.Task
import scalaz.stream.Process

object PeakInfo {
  def find(minElev: Int): Reader[PeakRepository, Process[Task, Peak]] =
    Reader(_.find(minElev))

  def findOne(id: Int): Reader[PeakRepository, Task[Throwable \/ Option[Peak]]] =
    Reader(_.findOne(id))

  def insert(newPeak: NewPeak): Reader[PeakRepository, Task[Throwable \/ Peak]] =
    Reader(_.insert(newPeak))

  def findVisible(minElev: Int, loc: Location3D): Reader[PeakRepository, Task[Vector[VisiblePeak]]] =
    for {
      process <- find(minElev)
    } yield process.filter(_.isVisibleFrom(loc)).map { peak =>
      val dist = loc.distanceTo(peak)
      val bearing = loc.bearingTo(peak)
      VisiblePeak(peak.id, peak.name, peak.elevation, bearing, dist)
    }.runLog
}
