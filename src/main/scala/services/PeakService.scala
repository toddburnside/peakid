package services

import models.Peak

import io.circe._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl._
import repositories.PeakRepository
import scalaz.concurrent.Task
import scalaz._

trait PeakService {
  self: PeakRepository =>

  // TODO: Move these
  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) = org.http4s.circe.jsonOf[A]
  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]) = org.http4s.circe.jsonEncoderOf[A]

  // TODO: Move this
  def eitherToResponse[A](e: Throwable \/ A)(f: A => Task[Response]): Task[Response] =
    e.fold(l => InternalServerError(l.getMessage), r => f(r))

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
