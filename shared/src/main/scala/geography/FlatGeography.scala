package geography

trait FlatGeography[A] {
  def lon: Double
  def lat: Double

  // distance is in km
  def distanceTo[B](that: FlatGeography[B]): Double =
    FlatGeography.distanceBetween(this, that)

  def bearingTo[B](that: FlatGeography[B]): Double =
    FlatGeography.bearingBetween(this, that)

  def destination(distance: Double, bearing: Double): (Double, Double) =
    FlatGeography.destination(this, distance, bearing)
}

object FlatGeography {

  // This algorithm is from http://www.movable-type.co.uk/scripts/latlong.html
  // It uses the "Sperical Law of Cosines", which is probably more than
  // accurate enough. For more accuracy with more computational expense, there is
  // also the "haversine formula".
  // Note also that this is the distance on a great circle, not a rhumb
  // line. But, this is close enough for the distances we can see
  // other mountains at, and doesn't have the issues near the poles.
  // distance is in km
  def distanceBetween[A, B](a: FlatGeography[A], b: FlatGeography[B]): Double = {
    val φ1 = a.lat.toRadians
    val φ2 = b.lat.toRadians
    val deltaλ = (b.lon - a.lon).toRadians
    math.acos(math.sin(φ1) * math.sin(φ2) + math.cos(φ1) * math.cos(φ2) * math.cos(deltaλ)) * R
  }

  // Bearing is also from http://www.movable-type.co.uk/scripts/latlong.html
  // It is actually the initial bearing for a path along a great circle,
  // not along a rhumb line. For the distances in the application, it should
  // be adequate.
  def bearingBetween[A, B](start: FlatGeography[A], end: FlatGeography[B]): Double = {
    val λ1 = start.lon.toRadians
    val φ1 = start.lat.toRadians
    val λ2 = end.lon.toRadians
    val φ2 = end.lat.toRadians
    val φ2Cos = math.cos(φ2)
    val y = math.sin(λ2 - λ1) * φ2Cos
    val x = math.cos(φ1) * math.sin(φ2) - math.sin(φ1) * φ2Cos * math.cos(λ2 - λ1)
    math.atan2(y, x).toDegrees
  }

  // Also from http://www.movable-type.co.uk/scripts/latlong.html.
  // Another great circle calculation. Distance in km, bearing is initial bearing.
  // Returns (lon: Double, lat: Double)
  def destination[A](start: FlatGeography[A], distance: Double, bearing: Double): (Double, Double) = {
    val λ1 = start.lon.toRadians
    val φ1 = start.lat.toRadians
    val φ1Sin = math.sin(φ1)
    val φ1Cos = math.cos(φ1)
    val θ = bearing.toRadians
    val δ = distance / R // angular distance
    val δSin = math.sin(δ)
    val δCos = math.cos(δ)
    val φ2 = math.asin(φ1Sin * δCos + φ1Cos * δSin * math.cos(θ))
    val λ2 = λ1 + math.atan2(math.sin(θ) * δSin * φ1Cos, δCos - φ1Sin * math.sin(φ2))
    val lat2 = φ2.toDegrees
    // normalize lon2 to -180, 180
    val lon2 = (λ2.toDegrees + 540) % 360 - 180
    (lon2, lat2)
  }
}