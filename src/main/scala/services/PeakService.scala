package services

//import free.peakData._
import elevation.{ElevationInfo, ElevationProvider}
import models.{Location3D, VisiblePeak}
import io.circe.generic.auto._
import models.PeakBase.{NewPeak, Peak}
import org.http4s._
import org.http4s.dsl._
import repositories.{PeakInfo, PeakRepository}
import fs2.{Stream, Task}
import fs2.interop.cats._
import cats.~>
import cats.data._
import cats.implicits._
import doobie.imports.Transactor
import free.freeElevation.Elevation
import free.peakData.{PeakData, PeakDatas}

class PeakService(xa: Transactor[Task], peakRepo: PeakRepository, elevProvider: ElevationProvider) extends BaseService {
  type PeakIdApp[A] = Coproduct[PeakData, Elevation, A]
  val interpreter: PeakIdApp ~> Task = free.peakData.doobiePeakDataInterpreter(xa) or free.freeElevation.googleElevationInterpreter(elevProvider)
  def service = HttpService {
    // get all the peaks theoretically visible from the location provided.
    case GET -> Root :? LonMatcher(lon) +& LatMatcher(lat)
      +& OptElevMatcher(optElev) +& OptMinElevMatcher(optMinElev) => {
      // work inside EitherT so we can make use of the elevation easily
//      var peaksT: EitherT[Task, Throwable, Vector[VisiblePeak]] = for {
      var peaksT: EitherT[Task, Throwable, Stream[Task, VisiblePeak]] = for {
        // if the elevation was provided, convert to a Task[disjunction], else
        // if the elevation wasn't provided, go get it for the location.
        elevation <- EitherT(optElev.map(e => Task.now(e.asRight)).
          getOrElse(ElevationInfo.getElevation(lon, lat).run(elevProvider)))

        // the mininum elevation of the peaks to include in the result
        minElev = optMinElev.getOrElse(0)

        peaks = PeakInfo.findVisible(minElev, Location3D(lon, lat, elevation)).run(peakRepo)
      } yield peaks

      // Extract the Task[Either] and convert to a response
      peaksT.value.flatMap(p => eitherToResponse(p)(Ok(_)))
    }

    // get an individual peak by id
    case GET -> Root / IntVar(id) => {
//      def program(implicit P: PeakDatas[PeakData]) = {
//        import P._
//        for {
//          opvFree <- findOnePeak(id)
//        } yield opvFree
//      }
      def program(implicit P: PeakDatas[PeakData]) = P.findOnePeak(id)

//      val x: Task[Either[Throwable, Option[Peak]]] = program.foldMap(interpreter)
      val x: Task[Either[Throwable, Option[Peak]]] = program.foldMap(free.peakData.doobiePeakDataInterpreter(xa))
      x.flatMap(opv => eitherToResponse(opv)(_.fold(NotFound())(Ok(_))))
    }
//    case GET -> Root / IntVar(id) => for {
//      opv <- PeakInfo.findOne(id).run(peakRepo)
//      result <- eitherToResponse(opv)(_.fold(NotFound())(Ok(_)))
//    } yield result

    // add a new peak
    case req@POST -> Root =>
      req.decode[NewPeak] { p =>
        for {
          peakView <- PeakInfo.insert(p).run(peakRepo)
          result <- eitherToResponse(peakView)(Ok(_))
        } yield result
      }
  }
}
