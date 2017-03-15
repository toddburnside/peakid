package repositories

import models.PeakBase.{NewPeak, Peak}

import fs2.{Stream, Task}

trait PeakRepository {
  def findOne(id: Int): Task[Throwable Either Option[Peak]]
  def find(minElev: Int): Stream[Task, Peak]
  def insert(newPeak: NewPeak): Task[Throwable Either Peak]
}

