package models

import doobie.imports.Meta
import doobie.contrib.postgresql.pgtypes._
import org.postgis.Point

case class Location(lon: Double, lat: Double)

object Location {
  implicit val locationMeta: Meta[Location] =
    Meta[Point].nxmap(p => new Location(p.x, p.y), l => new Point(l.lon, l.lat))
}
