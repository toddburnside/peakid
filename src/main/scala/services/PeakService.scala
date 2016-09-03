package services

import elevation.{ElevationInfo, ElevationProvider}
import models.{Location3D, Peak}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl._
import repositories.{PeakInfo, PeakRepository}

import scalaz.concurrent.Task

class PeakService(val peakRepo: PeakRepository, elevProvider: ElevationProvider) extends BaseService {
  def service = HttpService {
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)
      +& OptElevMatcher(optElev) +& OptMinElevMatcher(optMinElev) => for {
      elevation <- optElev.fold(ElevationInfo.getElevation(lon, lat).run(elevProvider))(Task.now)
      minElev = optMinElev.getOrElse(0)
      peaks <- PeakInfo.findVisible(minElev, Location3D(lon, lat, elevation)).run(peakRepo)
      result <- Ok(peaks)
    } yield result

    case GET -> Root / IntVar(id) => for {
      opv <-  PeakInfo.findOne(id).run(peakRepo)
      result <- eitherToResponse(opv)(_.fold(NotFound())(Ok(_)))
    } yield result

    case req@POST -> Root =>
      req.decode[Peak] { p => for {
        peakView <- PeakInfo.insert(p).run(peakRepo)
        result <- eitherToResponse(peakView)(Ok(_))
      } yield result
    }
  }
}
