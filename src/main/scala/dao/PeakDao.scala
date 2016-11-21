package dao

import doobie.imports._
import models.PeakBase.{NewPeak, Peak}

trait PeakDao {
  // TODO: Limit the results on the server side using GIS stuff.
  def findOneQuery(id: Int): Query0[Peak] = //ConnectionIO[Throwable \/ Option[PeakView]] =
    sql"""
         select id, name, usgsid, state, county, map, elevation, location
         from peaks
         where id = $id""".query[Peak]

  def findQuery(minElev: Int): Query0[Peak] =
    sql"""
          select id, name, usgsid, state, county, map, elevation, location
          from peaks
          where elevation > $minElev""".query[Peak]

  // TODO: unique constraint for usgsid
  def insertQuery(newPeak: NewPeak): Update0 = // ConnectionIO[Throwable \/ PeakView] =
    sql"""
    insert into peaks (name, usgsid, state, county, map, elevation, location)
    values (${newPeak.name}, ${newPeak.usgsid}, ${newPeak.state},
            ${newPeak.county}, ${newPeak.map}, ${newPeak.elevation},
            ST_SetSRID(${newPeak.location}::GEOMETRY, 4326))
    """.update
}
