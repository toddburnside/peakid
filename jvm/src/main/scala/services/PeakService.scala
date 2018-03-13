package services

import cats._
import elevation.{ElevationInfo, ElevationProvider}
import models.{Location3D, VisiblePeak}
import io.circe.generic.auto._
import org.http4s._
import repositories.{PeakInfo, PeakRepository}
import cats.data.EitherT
import cats.effect.Effect
import cats.implicits._

class PeakService[F[_]: Effect](val peakRepo: PeakRepository[F],
                                elevProvider: ElevationProvider[F])
    extends BaseService[F] {
  def service: HttpService[F] = HttpService[F] {
    // get all the peaks theoretically visible from the location provided.
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)
          +& OptElevMatcher(optElev) +& OptMinElevMatcher(optMinElev) => {

      // if the elevation was provided, convert to a F[Either[Throwable, Int], else
      // if the elevation wasn't provided, go get it for the location.
      val fEitherElev = optElev
        .map(e => Applicative[F].pure(e.asRight[Throwable]))
        .getOrElse(ElevationInfo.getElevation[F](lon, lat).run(elevProvider))

      // work inside EitherT so we can make use of the elevation easily
      val peaksT: EitherT[F, Throwable, Vector[VisiblePeak]] = for {
        elevation <- EitherT[F, Throwable, Int](fEitherElev)

        // the minimum elevation of the peaks to include in the result - default to zero
        minElev = optMinElev.getOrElse(0)

        peaks <- EitherT(
          PeakInfo
            .findVisible[F](minElev, Location3D(lon, lat, elevation))
            .run(peakRepo))
      } yield peaks

      // Extract the Task[Either] and convert to a response
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
