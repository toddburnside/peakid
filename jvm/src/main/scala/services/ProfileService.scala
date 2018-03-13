package services

import cats.implicits._
import cats.effect.Effect
import elevation.{ElevationInfo, ElevationProvider}
import org.http4s._

class ProfileService[F[_]: Effect](val elevProvider: ElevationProvider[F])
    extends BaseService[F] {
  def service = HttpService[F] {
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat) =>
      for {
        elevation <- ElevationInfo.getElevation(lon, lat).run(elevProvider)
        result <- eitherToResponse(elevation)(Ok(_))
      } yield result
  }
}
