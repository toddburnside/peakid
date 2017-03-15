package repositories

import dao.PeakDao
import doobie.imports._

import cats.implicits._
import fs2.interop.cats._
import models.PeakBase.{Peak, NewPeak}

import fs2.Task

class PeakRepositoryDb(val xa: Transactor[Task]) extends PeakRepository with PeakDao {
  def findOne(id: Int) =
    findOneQuery(id)
      .option
      .attempt
      .transact(xa)

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

