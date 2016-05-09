package services

import dao.DoobieTransactor
import doobie.imports._
import repositories.PeakRepositoryDb

import scalaz.concurrent.Task

case class PeakServiceDb(val xa: Transactor[Task])
  extends PeakService with PeakRepositoryDb with DoobieTransactor {
  val transactor = xa
}
