package repositories

import models.PeakBase.{NewPeak, Peak}
import fs2.{Stream}

trait PeakRepository[F[_]] {
  def findOne(id: Int): F[Throwable Either Option[Peak]]
  def find(minElev: Int): Stream[F, Peak]
  def insert(newPeak: NewPeak): F[Throwable Either Peak]
}
