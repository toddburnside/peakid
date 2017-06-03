package models

case class GoogleLocation(lat: Double, lng: Double)
case class ElevationResult(elevation: Double, location: GoogleLocation, resolution: Double)
case class GoogleElevationResponse(status: String, results: List[ElevationResult],
                                   error_message: Option[String])
