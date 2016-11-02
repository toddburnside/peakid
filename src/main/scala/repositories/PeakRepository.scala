package repositories

import models.PeakBase.{NewPeak, Peak}

import scalaz._
import scalaz.concurrent.Task
import scalaz.stream.Process

trait PeakRepository {
  def findOne(id: Int): Task[Throwable \/ Option[Peak]]
  def find(minElev: Int): Process[Task, Peak]
  def insert(newPeak: NewPeak): Task[Throwable \/ Peak]
}

