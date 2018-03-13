package services

import elevation.ElevationProvider
import models.PeakBase.{NewPeak, Peak}
import models._
import org.http4s.{Method, Request, Status, Uri}
import org.http4s.implicits._
import org.specs2.matcher.ResultMatchers
import org.specs2.mutable.Specification
import repositories.PeakRepository
import fs2.Stream
import cats.effect.IO
import cats.implicits._

// TODO: Thus far, this is only exploratory to see HOW to write tests on a service.
// TODO: Now, I need to write some actual tests...
trait PeakRepo extends PeakRepository[IO] {
  val newPeak =
    new NewPeak((), "Name", 1, "OR", "Lane", "themap", 1000, Location(1.1, 2.2))
  val peak =
    new Peak(1, "Name", 1, "OR", "Lane", "themap", 1000, Location(1.1, 2.2))
  val somePV = peak.some
  val eSomePV = somePV.asRight[Throwable]

  def find(minElev: Int): Stream[IO, Peak] = ???
  def findOne(id: Int) = IO.pure(eSomePV)
  def insert(newPeak: NewPeak) = IO.pure(peak.asRight)
}

object PeakServiceSpec extends Specification with ResultMatchers {

  "A simple test to get started" >> {
    "Get should be successful" >> {
      val repo = new PeakRepo {}
      val elevProvider = new ElevationProvider[IO] {
        def getElevation(lon: Double, lat: Double) = ???
      }
      val peakSvc = new PeakService(repo, elevProvider)
      val request = Request[IO](Method.GET, Uri(path = "/3"))
      peakSvc.service
        .orNotFound(request)
        .unsafeRunSync
        .status must_== Status.Ok
    }
  }
}
