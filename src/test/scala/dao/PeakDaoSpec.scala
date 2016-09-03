package dao

import doobie.contrib.specs2.analysisspec.AnalysisSpec
import doobie.imports._
import models.{PeakId, PeakView, Location, Peak}
import org.specs2.mutable.Specification

import scalaz.concurrent.Task

object PeakDaoSpec extends Specification with AnalysisSpec with PeakDao {
  // TODO: These shouldn't be hardcoded, plus it all should probably be moved to a trait or helper class
  val url = "jdbc:postgresql://192.168.0.106:5432/peakid"
  val user = "postgres"
  val pass = ""
  override val transactor = DriverManagerTransactor[Task]("org.postgresql.Driver", url, user, pass)

  // the actual values don't matter
  val peak = Peak("Name", 1, "OR", "Lane", "themap", 1000, Location(1.1, 2.2))
  val peakView = PeakView(PeakId(1), peak)

  check(findQuery(0))
  check(findOneQuery(1))
  check(insertQuery(peak))
}
