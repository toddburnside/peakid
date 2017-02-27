package peakid

import java.io.File

import knobs.{FileResource, Required}
import org.http4s.server.{Router, ServerApp}
import org.http4s.server.blaze.BlazeBuilder
import services.{PeakService, ProfileService}
import fs2.{Strategy, Task}
import doobie.imports._
import elevation.GoogleElevationProvider
import org.http4s.client.blaze.PooledHttp1Client
import repositories.PeakRepositoryDb

import scala.concurrent.ExecutionContext

object Main extends ServerApp {

  // just get the cats port working for now. This is for knobs, which I will replace with case classy
  implicit def toFs2Task[A](zTask: scalaz.concurrent.Task[A])(implicit strategy: Strategy): Task[A] =
    Task.async(cb => zTask.unsafePerformAsync(r => cb(r.toEither)))
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val strategy = Strategy.fromExecutionContext(implicitly[ExecutionContext])

  def server(args: List[String]) = {
    // TODO: Get Hikari to work
    for {
      cfg <- toFs2Task(knobs.loadImmutable(Required(FileResource(new File("peakid.cfg"))) :: Nil))
      url = cfg.require[String]("db.url")
      user = cfg.require[String]("db.user")
      pass = cfg.require[String]("db.pass")
      googleKey = cfg.require[String]("googleElevation.key")

      xa = DriverManagerTransactor[Task]("org.postgresql.Driver", url, user, pass)
      peakRepo = new PeakRepositoryDb(xa)
      client = PooledHttp1Client()
      elevProvider = new GoogleElevationProvider(googleKey, client)

      service = Router(
        "/peaks" -> new PeakService(peakRepo, elevProvider).service,
        "/profiles" -> new ProfileService(elevProvider).service)
      svr <- BlazeBuilder.bindHttp(8080)
        .mountService(service, "/api")
        .start
    } yield svr
  }
}
