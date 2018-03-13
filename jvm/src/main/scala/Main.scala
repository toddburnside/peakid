package peakid

import org.http4s.server.blaze.BlazeBuilder
import services.{PeakService, ProfileService, StaticFileService}
import doobie.hikari._
import elevation._
import org.http4s.client.blaze.Http1Client
import org.http4s.server.middleware.CORS
import repositories.PeakRepositoryDb
import cats.effect.{Effect, IO}
import cats.implicits._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends HttpServer[IO]

class HttpServer[F[_]: Effect] extends StreamApp[F] {
  override def stream(args: List[String],
                      requestShutdown: F[Unit]): Stream[F, ExitCode] = {
    AppConfig.load.fold(
      error =>
        Stream.raiseError(new Exception(s"Configuration Error: ${error}")),
      config => createServer(config))
  }

  def createServer(appConfig: AppConfig): Stream[F, ExitCode] = {
    for {
      // xa and client are created in Stream.brackets, so are self-cleaning.
      xa <- HikariTransactor.stream(appConfig.db.driver,
                                    appConfig.db.url,
                                    appConfig.db.user,
                                    appConfig.db.pass)
      client <- Http1Client.stream()

      peakRepo = new PeakRepositoryDb(xa)
      elevProvider = appConfig.elevationProvider match {
        case GoogleApi() =>
          new GoogleElevationProvider[F](appConfig.google.key, client)
        case NationalMaps() => new NationalMapElevationProvider[F](client)
      }

      peakSvc = CORS(new PeakService[F](peakRepo, elevProvider).service)
      profileSvc = new ProfileService[F](elevProvider).service
      staticSvc = new StaticFileService[F]().service

      svr <- BlazeBuilder[F]
        .bindHttp(appConfig.server.port, appConfig.server.host)
        .mountService(peakSvc, "/api/peaks")
        .mountService(profileSvc, "/api/profiles")
        .mountService(staticSvc, "/")
        .serve
    } yield svr
  }
}
