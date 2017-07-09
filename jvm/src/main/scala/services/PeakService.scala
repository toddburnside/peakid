package services

import elevation.{ElevationInfo, ElevationProvider}
import models.{Location3D, VisiblePeak}
import io.circe.generic.auto._
import models.PeakBase.NewPeak
import org.http4s._
import org.http4s.dsl._
import repositories.{PeakInfo, PeakRepository}

import fs2.Task
import fs2.interop.cats._
import cats.data._, cats.implicits._

class PeakService(val peakRepo: PeakRepository,
                  elevProvider: ElevationProvider)
    extends BaseService {
  def service = HttpService {
    // get all the peaks theoretically visible from the location provided.
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)
          +& OptElevMatcher(optElev) +& OptMinElevMatcher(optMinElev) => {
      // work inside EitherT so we can make use of the elevation easily
      var peaksT: EitherT[Task, Throwable, Vector[VisiblePeak]] = for {
        // if the elevation was provided, convert to a Task[disjunction], else
        // if the elevation wasn't provided, go get it for the location.
        elevation <- EitherT(
          optElev
            .map(e => Task.now(e.asRight))
            .getOrElse(ElevationInfo.getElevation(lon, lat).run(elevProvider)))

        // the mininum elevation of the peaks to include in the result
        minElev = optMinElev.getOrElse(0)

        peaks <- EitherT(
          PeakInfo
            .findVisible(minElev, Location3D(lon, lat, elevation))
            .run(peakRepo))
      } yield peaks

      // Extract the Task[\/] and convert to a response
      peaksT.value.flatMap(p => eitherToResponse(p)(Ok(_)))
    }

    // get an individual peak by id
    case GET -> Root / IntVar(id) =>
      for {
        opv <- PeakInfo.findOne(id).run(peakRepo)
        result <- eitherToResponse(opv)(_.fold(NotFound())(Ok(_)))
      } yield result

    // add a new peak
//    case req @ POST -> Root =>
//      req.decode[NewPeak] { p =>
//        for {
//          peakView <- PeakInfo.insert(p).run(peakRepo)
//          result <- eitherToResponse(peakView)(Ok(_))
//        } yield result
//      }
  }
}
