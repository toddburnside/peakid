package geography

trait ElevatedGeography[A] extends FlatGeography[A] {
  def lon: Double
  def lat: Double
  def elevation: Int // in meters

  def toHorizon: Double = ElevatedGeography.toHorizon(this)

  def isVisibleFrom[B](b: ElevatedGeography[B]): Boolean =
    ElevatedGeography.isVisibleFrom(this, b)
}

object ElevatedGeography {
  // This simplification came from wikipedia Horizon entry, and essentially
  // ignores the curvature of the earth and measures from the altitude to
  // the horizon, not along the curvature.
  // I derived an equation that measures the distance along the surface of
  // the earth: d = 2 * pi * R / 360 * arccos(R/R+h)
  // However, even at 7000m elevation, they were within 0.2km (298.5km)
  // This is way less significant than the deviation from a sphere and
  // the refraction.
  // result is in km
  def toHorizon[A](a: ElevatedGeography[A]): Double = {
    // For geometric calculation without refraction, the constant below would
    // be 3.57. The 3.86 adds an extra 8% for refraction at standard atmospheric
    // conditions. Temperature gradients, etc., can greatly affect this.
    3.86 * math.sqrt(a.elevation.toDouble)
  }

  // If the distance to the horizon for each of the points overlaps, then one
  // should be visible from the other.
  def isVisibleFrom[A, B](a: ElevatedGeography[A], b: ElevatedGeography[B]): Boolean = {
    a.toHorizon + b.toHorizon > a.distanceTo(b)
  }
}