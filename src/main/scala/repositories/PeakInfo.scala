package repositories

//import cats.data.Reader
import models.{Location3D, Peak, PeakView, VisiblePeak}

import scalaz._
import scalaz.concurrent.Task
import scalaz.stream.Process

object PeakInfo {
  def find(minElev: Int): Reader[PeakRepository, Process[Task, PeakView]] =
    Reader(_.find(minElev))

  def findOne(id: Int): Reader[PeakRepository, Task[Throwable \/ Option[PeakView]]] =
    Reader(_.findOne(id))

  def insert(newPeak: Peak): Reader[PeakRepository, Task[Throwable \/ PeakView]] =
    Reader(_.insert(newPeak))

  def findVisible(minElev: Int, loc: Location3D): Reader[PeakRepository, Task[Vector[VisiblePeak]]] =
    for {
      process <- find(minElev)
    } yield process.filter(_.peak.isVisibleFrom(loc)).map { pv =>
      val peak = pv.peak
      val dist = loc.distanceTo(peak)
      val bearing = loc.bearingTo(peak)
      VisiblePeak(pv.id.id, peak.name, peak.elevation, bearing, dist)
    }.runLog
}
