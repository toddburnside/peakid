package models

import geography.FlatGeography

case class Location(lon: Double, lat: Double)

object Location {

  implicit def flatGeographyLocation(a: Location): FlatGeography[Location] =
    new FlatGeography[Location] {
      override val lat: Double = a.lat
      override val lon: Double = a.lon
    }
}
