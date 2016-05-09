package repositories

import models.{Peak, PeakView}

import scalaz._
import scalaz.concurrent.Task

trait PeakRepository {
  def findOne(id: Int): Task[Throwable \/ Option[PeakView]]
  def insert(newPeak: Peak): Task[Throwable \/ PeakView]
}

