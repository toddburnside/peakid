package services

import models.{Location, Peak}

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

object PeakService {
  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) = org.http4s.circe.jsonOf[A]
  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]) = org.http4s.circe.jsonEncoderOf[A]

  val peak = Peak("Hunchback Mountain", 23, "OR", "Clackamas", "little map", 4000, Location(1.0, 3.4))
  val service = HttpService {
    case GET -> Root / "peaks" => Ok(peak)
  }
}
