package peakid

import java.io.File

import knobs.{FileResource, Required}
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.{Router, ServerApp}
import org.http4s.server.blaze.BlazeBuilder
import services.{PeakService, ProfileService}

import scalaz.concurrent.Task
import scalaz._
import Scalaz._
import doobie.imports._
import elevation.GoogleElevationProvider
import org.http4s.client.blaze.PooledHttp1Client
import repositories.PeakRepositoryDb

object Main extends ServerApp {

  def server(args: List[String]) = {
    // TODO: Get Hikari to work
    for {
      cfg <- knobs.loadImmutable(Required(FileResource(new File("peakid.cfg"))) :: Nil)
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
