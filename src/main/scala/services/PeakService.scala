package services

import dao.PeakDao
import doobie.imports._
import models.Peak

import io.circe._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl._
import scalaz.concurrent.Task
import scalaz._

// TODO: Refactor to make testable
object PeakService extends PeakDao {
  // TODO: Move these
  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) = org.http4s.circe.jsonOf[A]
  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]) = org.http4s.circe.jsonEncoderOf[A]

  // TODO: Move this
  def eitherToResponse[A](e: Throwable \/ A)(f: A => Task[Response]): Task[Response] =
    e.fold(l => InternalServerError(l.getMessage), r => f(r))

  def service(xa: Transactor[Task]) = HttpService {
    case GET -> Root / "peaks" / IntVar(id) => for {
      opv <- findOne(id).transact(xa)
      result <- eitherToResponse(opv)(_.fold(NotFound())(Ok(_)))
    } yield result

    case req@POST -> Root / "peaks" =>
      req.decode[Peak] { p => for {
        peakView <- insert(p).transact(xa)
        result <- eitherToResponse(peakView)(Ok(_))
      } yield result
    }
  }
}
