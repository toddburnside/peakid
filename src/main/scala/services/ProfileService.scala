package services

import elevation.{ElevationInfo, ElevationProvider}
import org.http4s._
import org.http4s.dsl._

class ProfileService(val elevProvider: ElevationProvider) extends BaseService {
  def service = HttpService {
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)  => for {
      elevation <- ElevationInfo.getElevation(lon, lat).run(elevProvider)
      result <- eitherToResponse(elevation)(Ok(_))
    } yield result
  }
}
