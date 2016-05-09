package services

import models.{PeakId, Location, Peak, PeakView}
import org.http4s.{Status, Method, Request, Uri}
import org.specs2.matcher.{ResultMatchers, TaskMatchers}
import org.specs2.mutable.Specification
import repositories.PeakRepository

import scalaz._, Scalaz._
import scalaz.concurrent.Task

// TODO: Thus far, this is only exploratory to see HOW to write tests on a service.
// TODO: Now, I need to write some actual tests...
trait PeakRepo extends PeakRepository {
  val peak = Peak("Name", 1, "OR", "Lane", "themap", 1000, Location(1.1, 2.2))
  val peakView = PeakView(PeakId(1), peak)
  val somePV = peakView.some
  val eSomePV = somePV.right[Throwable]

  def findOne(id: Int) = Task.now(eSomePV)
  def insert(newPeak: Peak) = Task.now(peakView.right)
}

object PeakServiceSpec extends Specification with TaskMatchers with ResultMatchers
  with PeakService with PeakRepo {

  "A simple test to get started" >> {
    "Get should be successful" >> {
      implicit val x = Task.now(peakView.some.right[Throwable])
      val request = Request(Method.GET, Uri(path = "/peaks/3"))
      val responseTask = service =<< Task.now(request)
      val response = responseTask.unsafePerformSync
      response.status must_== Status.Ok
    }
  }
}
