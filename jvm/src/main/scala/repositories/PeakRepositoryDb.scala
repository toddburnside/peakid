package repositories

import cats.effect.Effect
import dao.PeakDao
import doobie._
import doobie.implicits._
import cats.implicits._
import models.PeakBase.{NewPeak, Peak}

class PeakRepositoryDb[F[_]: Effect](val xa: Transactor[F])
    extends PeakRepository[F]
    with PeakDao {
  def findOne(id: Int): F[Either[Throwable, Option[Peak]]] =
    findOneQuery(id).option.attempt
      .transact(xa)

  def find(minElev: Int): fs2.Stream[F, Peak] =
    findQuery(minElev).stream
      .transact(xa)

  def insert(newPeak: NewPeak) =
    insertQuery(newPeak)
      .withUniqueGeneratedKeys[Peak]("id",
                                     "name",
                                     "usgsid",
                                     "state",
                                     "county",
                                     "map",
                                     "elevation",
                                     "location")
      .attempt
      .transact(xa)
}
