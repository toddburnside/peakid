package dao

import doobie.imports._
import models.{PeakView, Peak}
import scalaz._, Scalaz._

// TODO: These should only return Query, etc., so they can be tested. The rest should be done elsewhere
class PeakDao {
  def findOne(id: Int): ConnectionIO[Throwable \/ Option[PeakView]] =
    sql"""
         select id, name, usgsid, state, county, map, elevation, location
         from peaks
         where id = $id""".query[PeakView].option.attempt

  // TODO: unique constraint for usgsid
  def insert(newPeak: Peak): ConnectionIO[Throwable \/ PeakView] =
    sql"""
    insert into peaks (name, usgsid, state, county, map, elevation, location)
    values (${newPeak.name}, ${newPeak.usgsid}, ${newPeak.state},
            ${newPeak.county}, ${newPeak.map}, ${newPeak.elevation},
            ST_SetSRID(${newPeak.location}::GEOMETRY, 4326))
    """.update
      .withUniqueGeneratedKeys[PeakView]("id", "name", "usgsid", "state", "county", "map", "elevation", "location")
      .attempt
}
