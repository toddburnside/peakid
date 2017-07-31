package peakid

import org.http4s.server.{Router, Server, ServerApp}
import org.http4s.server.blaze.BlazeBuilder
import services.{PeakService, ProfileService, StaticFileService}
import fs2.Task
import doobie.imports._
import doobie.hikari.imports._
import elevation._
import org.http4s.client.blaze.PooledHttp1Client
import org.http4s.server.middleware.CORS
import repositories.PeakRepositoryDb
import cats.implicits._

object Main extends ServerApp {

  def server(args: List[String]): Task[Server] = {
    AppConfig.load.fold(
      error => Task.fail(new Exception(s"Configuration Error: ${error}")),
      config => createServer(config))
  }

  def newConnection(db: DB): Task[Transactor[Task]] =
    HikariTransactor[Task](db.driver, db.url, db.user, db.pass)

  def createServer(appConfig: AppConfig): Task[Server] = {
    for {
      xa <- newConnection(appConfig.db)
      peakRepo = new PeakRepositoryDb(xa)
      client = PooledHttp1Client()
      elevProvider = appConfig.elevationProvider match {
        case GoogleApi() =>
          new GoogleElevationProvider(appConfig.google.key, client)
        case NationalMaps() => new NationalMapElevationProvider(client)
      }

      service = Router(
        "/api" -> Router(
          "/peaks" -> (CORS(new PeakService(peakRepo, elevProvider).service)),
          "/profiles" -> new ProfileService(elevProvider).service)) |+| StaticFileService.service
      svr <- BlazeBuilder
        .bindHttp(appConfig.server.port, appConfig.server.host)
        .mountService(service, "/")
        .start
    } yield svr
  }
}
