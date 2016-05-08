package peakid

import java.io.File

import knobs.{Required, FileResource}
import org.http4s.server.ServerApp
import org.http4s.server.blaze.BlazeBuilder
import scalaz.concurrent.Task

import doobie.imports._

object Main extends ServerApp {

  def server(args: List[String]) = {

    // TODO: Get Hikari to work
    for {
      cfg <- knobs.loadImmutable(Required(FileResource(new File("peakid.cfg"))) :: Nil)
      url = cfg.require[String]("db.url")
      user = cfg.require[String]("db.user")
      pass = cfg.require[String]("db.pass")
      xa = DriverManagerTransactor[Task]("org.postgresql.Driver", url, user, pass)
      svr <- BlazeBuilder.bindHttp(8080)
      .mountService(services.PeakService.service(xa), "/api")
      .start
    } yield svr
  }
}
