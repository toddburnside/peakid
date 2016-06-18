package services

import org.http4s.dsl._ //ValidatingQueryParamDecoderMatcher

object QueryParams {
  object LonMatcher extends ValidatingQueryParamDecoderMatcher[Double]("lon")
  object LatMatcher extends ValidatingQueryParamDecoderMatcher[Double]("lat")
//  object LonMatcher extends QueryParamDecoderMatcher[Double]("lon")
//  object LatMatcher extends QueryParamDecoderMatcher[Double]("lat")
}
