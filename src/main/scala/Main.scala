package peakid

import org.http4s.server.blaze.BlazeBuilder

object Main {
  def main(args: Array[String]): Unit = {
    BlazeBuilder.bindHttp(8080)
      .mountService(services.PeakService.service, "/api")
      .run
      .awaitShutdown()
  }
}
