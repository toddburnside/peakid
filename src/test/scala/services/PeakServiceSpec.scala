package services

import elevation.ElevationProvider
import models._
import org.http4s.{Method, Request, Status, Uri}
import org.specs2.matcher.{ResultMatchers, TaskMatchers}
import org.specs2.mutable.Specification
import repositories.PeakRepository

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream.Process

// TODO: Thus far, this is only exploratory to see HOW to write tests on a service.
// TODO: Now, I need to write some actual tests...
trait PeakRepo extends PeakRepository {
  val peak = Peak("Name", 1, "OR", "Lane", "themap", 1000, Location(1.1, 2.2))
  val peakView = PeakView(PeakId(1), peak)
  val somePV = peakView.some
  val eSomePV = somePV.right[Throwable]


  def find(minElev: Int): Process[Task, PeakView] = ???
  def findOne(id: Int) = Task.now(eSomePV)
  def insert(newPeak: Peak) = Task.now(peakView.right)
}

object PeakServiceSpec extends Specification with TaskMatchers with ResultMatchers {

  "A simple test to get started" >> {
    "Get should be successful" >> {
      val repo = new PeakRepo {}
      val elevProvider = new ElevationProvider {
        def getElevation(lon: Double, lat: Double) = ???
      }
      var peakSvc = new PeakService(repo, elevProvider)
      val request = Request(Method.GET, Uri(path = "/3"))
      val responseTask = peakSvc.service.run(request)
      val response = responseTask.unsafePerformSync
      response.status must_== Status.Ok
    }
  }
}
