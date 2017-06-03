package dao

import doobie.specs2.imports._
import doobie.imports._
import models.Location
import models.PeakBase.NewPeak
import org.specs2.mutable.Specification

object PeakDaoSpec extends Specification with AnalysisSpec with PeakDao {
  // TODO: These shouldn't be hardcoded, plus it all should probably be moved to a trait or helper class
  val url = "jdbc:postgresql://localhost:5432/peakid"
  val user = "postgres"
  val pass = ""
  override val transactor = DriverManagerTransactor[IOLite]("org.postgresql.Driver", url, user, pass)

  // the actual values don't matter
  val newPeak = new NewPeak((), "Name", 1, "OR", "Lane", "themap", 1000, Location(1.1, 2.2))

  check(findQuery(0))
  check(findOneQuery(1))
  check(insertQuery(newPeak))
}
