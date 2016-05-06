package peakid

import org.http4s.server.ServerApp
import org.http4s.server.blaze.BlazeBuilder
import scalaz.concurrent.Task

import doobie.imports._
//import doobie.contrib.hikari.hikaritransactor._

object Main extends ServerApp {

//  def tmain: Task[Server] =
//    for {
//      xa <- HikariTransactor[Task]("org.postgresql.Driver", "jdbc:postgresql://192.168.0.107:5432/peakid", "postgres", "")
//      server <- BlazeBuilder.bindHttp(8080)
//        .mountService(services.PeakService.service(xa), "/api")
//        .start
//      _  <- xa.shutdown
//    } yield (server)

  def server(args: List[String]) = {
//    tmain
    // TODO: Get Hikari to work
    val xa = DriverManagerTransactor[Task]("org.postgresql.Driver", "jdbc:postgresql://192.168.0.107:5432/peakid", "postgres", "")
    BlazeBuilder.bindHttp(8080)
      .mountService(services.PeakService.service(xa), "/api")
      .start
  }
}
