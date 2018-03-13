package dao

import cats.effect.IO
import doobie.specs2._
import doobie.util.transactor.Transactor
import models.Location
import models.PeakBase.NewPeak
import org.specs2.mutable.Specification

object PeakDaoSpec extends Specification with IOChecker with PeakDao {
  // TODO: These shouldn't be hardcoded, plus it all should probably be moved to a trait or helper class
  val url = "jdbc:postgresql://localhost:5432/peakid"
  val user = "postgres"
  val pass = ""
  override val transactor =
    Transactor.fromDriverManager[IO]("org.postgresql.Driver", url, user, pass)

  // the actual values don't matter
  val newPeak =
    new NewPeak((), "Name", 1, "OR", "Lane", "themap", 1000, Location(1.1, 2.2))

  check(findQuery(0))
  check(findOneQuery(1))
  check(insertQuery(newPeak))
}
