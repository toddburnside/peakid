package models

import geography.{ElevatedGeography, FlatGeography}

case class PeakId(id: Int)

case class Peak(name: String, usgsid: Int, state: String, county: String,
                map: String, elevation: Int, location: Location)

case class PeakView(id: PeakId, peak: Peak)

object Peak {
  // TODO: Move this elsewhere?
  implicit def elevationGeographyPeak(a: Peak): ElevatedGeography[Peak] =
    new ElevatedGeography[Peak] {
      override val lat: Double = a.location.lat
      override val lon: Double = a.location.lon
      override val elevation: Int = a.elevation
    }
}