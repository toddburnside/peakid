package repositories

import dao.PeakDao
import doobie.imports._
import models.PeakBase.{Peak, NewPeak}

import scalaz._
import Scalaz._
import scalaz.concurrent.Task

class PeakRepositoryDb(val xa: Transactor[Task]) extends PeakRepository with PeakDao {
  def findOne(id: Int) =
    findOneQuery(id)
      .option
      .attempt
      .transact(xa)

  // TODO: How do I handle exceptions? apparently, I can't use attempt...
  def find(minElev: Int) =
    findQuery(minElev)
      .process
      .transact(xa)

  def insert(newPeak: NewPeak) =
    insertQuery(newPeak)
      .withUniqueGeneratedKeys[Peak]("id", "name", "usgsid", "state", "county", "map", "elevation", "location")
      .attempt
      .transact(xa)
}

