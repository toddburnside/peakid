package models

import doobie.imports._
import doobie.postgres.pgistypes.PointType
import geography.FlatGeography
import org.postgis.Point

case class Location(lon: Double, lat: Double)

object Location {
  implicit val locationMeta: Meta[Location] =
    Meta[Point].nxmap(p => new Location(p.x, p.y), l => new Point(l.lon, l.lat))

  implicit def flatGeographyLocation(a: Location): FlatGeography[Location] =
    new FlatGeography[Location] {
      override val lat: Double = a.lat
      override val lon: Double = a.lon
    }
}
