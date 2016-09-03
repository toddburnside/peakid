package repositories

import models.{Peak, PeakView}

import scalaz._
import scalaz.concurrent.Task
import scalaz.stream.Process

trait PeakRepository {
  def findOne(id: Int): Task[Throwable \/ Option[PeakView]]
  def find(minElev: Int): Process[Task, PeakView]
  def insert(newPeak: Peak): Task[Throwable \/ PeakView]
}

