package repositories

import dao.{DoobieTransactor, PeakDao}
import doobie.imports._
import models.{PeakView, Peak}

import scalaz._, Scalaz._

trait PeakRepositoryDb extends PeakRepository with PeakDao {
  self: DoobieTransactor =>

  def findOne(id: Int) =
    findOneQuery(id)
      .option
      .attempt
      .transact(xa)

  def insert(newPeak: Peak) =
    insertQuery(newPeak)
      .withUniqueGeneratedKeys[PeakView]("id", "name", "usgsid", "state", "county", "map", "elevation", "location")
      .attempt.transact(xa)
}
