package services

import models.Peak

import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl._
import repositories.PeakRepository

trait PeakService extends BaseService {
  self: PeakRepository =>


  def service = HttpService {
    case GET -> Root / IntVar(id) => for {
      opv <- findOne(id)
      result <- eitherToResponse(opv)(_.fold(NotFound())(Ok(_)))
    } yield result

    case req@POST -> Root =>
      req.decode[Peak] { p => for {
        peakView <- insert(p)
        result <- eitherToResponse(peakView)(Ok(_))
      } yield result
    }
  }
}
