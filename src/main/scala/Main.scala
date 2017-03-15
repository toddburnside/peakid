package peakid

import org.http4s.server.{Router, Server, ServerApp}
import org.http4s.server.blaze.BlazeBuilder
import services.{PeakService, ProfileService}
import fs2.Task
import doobie.imports._
import doobie.hikari.imports._
import elevation.GoogleElevationProvider
import org.http4s.client.blaze.PooledHttp1Client
import repositories.PeakRepositoryDb

object Main extends ServerApp {

  def server(args: List[String]): Task[Server] = {
    AppConfig.load.fold(error => Task.fail(new Exception(s"Configuration Error: ${error}")),
      config => createServer(config))
  }

  def newConnection(db: DB): Task[Transactor[Task]] = HikariTransactor[Task](db.driver, db.url, db.user, db.pass)

  def createServer(appConfig: AppConfig): Task[Server] = {
    for {
      xa <- newConnection(appConfig.db)
      peakRepo = new PeakRepositoryDb(xa)
      client = PooledHttp1Client()
      elevProvider = new GoogleElevationProvider(appConfig.google.key, client)

      service = Router(
        "/peaks" -> new PeakService(peakRepo, elevProvider).service,
        "/profiles" -> new ProfileService(elevProvider).service)
      svr <- BlazeBuilder.bindHttp(8080)
        .mountService(service, "/api")
        .start
    } yield svr
  }
}
