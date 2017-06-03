package services

import fs2.interop.cats._
import elevation.{ElevationInfo, GoogleElevationProvider}
import org.http4s._
import org.http4s.dsl._

class ProfileService(val elevProvider: GoogleElevationProvider) extends BaseService {
  def service = HttpService {
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)  => {
//      def program = for {
//        elevationFree <- ElevationInfo.getFreeElevation(lon, lat)
//      } yield elevationFree
      def program = ElevationInfo.getFreeElevation(lon, lat)

      val e = program.foldMap(free.freeElevation.googleElevationInterpreter(elevProvider))
      e.flatMap(x => eitherToResponse(x)(Ok(_)))
    }
//    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)  => for {
//      elevation <- ElevationInfo.getElevation(lon, lat).run(elevProvider)
//      result <- eitherToResponse(elevation)(Ok(_))
//    } yield result
  }
}
