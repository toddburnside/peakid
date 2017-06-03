package models

import geography.ElevatedGeography

case class Location3D(lon: Double, lat: Double, elevation: Int)

object Location3D {
  implicit def elevatedGeographyLoc3D(a: Location3D) =
    new ElevatedGeography[Location3D] {
      override val lon: Double = a.lon

      override val lat: Double = a.lat

      override val elevation: Int = a.elevation
    }
}
