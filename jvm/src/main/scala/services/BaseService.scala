package services

import cats.effect.Effect
import io.circe.{Decoder, Encoder}
import org.http4s._
import org.http4s.dsl.Http4sDsl

class BaseService[F[_]: Effect] extends Http4sDsl[F] {
  // implicit en/decoders for Circe
  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) =
    org.http4s.circe.jsonOf[F, A]

  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]) =
    org.http4s.circe.jsonEncoderOf[F, A]

  // Takes an either with a throwable on the left and an A on the right.
  // If Left, return an InternalServiceError with the message, otherwise
  // uses the function passed it to create a response.
  def eitherToResponse[A](e: Throwable Either A)(
      f: A => F[Response[F]]): F[Response[F]] =
    e.fold(l => InternalServerError(l.getMessage), r => f(r))

  // Matchers for query parameters.
  //  object LonMatcher extends ValidatingQueryParamDecoderMatcher[Double]("lon")
  //  object LatMatcher extends ValidatingQueryParamDecoderMatcher[Double]("lat")
  object LonMatcher extends QueryParamDecoderMatcher[Double]("lon")

  object LatMatcher extends QueryParamDecoderMatcher[Double]("lat")

  object OptElevMatcher extends OptionalQueryParamDecoderMatcher[Int]("elev")

  object OptMinElevMatcher
      extends OptionalQueryParamDecoderMatcher[Int]("minElev")
}
