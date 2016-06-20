package services

import io.circe.{Decoder, Encoder}
import org.http4s.Response
import org.http4s.dsl._

import scalaz.\/
import scalaz.concurrent.Task

trait BaseService {
  // implicit en/decoders for Circe
  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) = org.http4s.circe.jsonOf[A]
  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]) = org.http4s.circe.jsonEncoderOf[A]

  // Takes an either with a throwable on the left and an A on the right.
  // If Left, return an InternalServiceError with the message, otherwise
  // uses the function passed it to create a response.
  def eitherToResponse[A](e: Throwable \/ A)(f: A => Task[Response]): Task[Response] =
    e.fold(l => InternalServerError(l.getMessage), r => f(r))

  // Matchers for query parameters.
//  object LonMatcher extends ValidatingQueryParamDecoderMatcher[Double]("lon")
//  object LatMatcher extends ValidatingQueryParamDecoderMatcher[Double]("lat")
    object LonMatcher extends QueryParamDecoderMatcher[Double]("lon")
    object LatMatcher extends QueryParamDecoderMatcher[Double]("lat")
}
