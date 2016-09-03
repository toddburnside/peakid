package dao

import doobie.imports._
import models.{PeakView, Peak}

trait PeakDao {
  // TODO: Need to use POST GIS to limit the results
  def findOneQuery(id: Int): Query0[PeakView] = //ConnectionIO[Throwable \/ Option[PeakView]] =
    sql"""
         select id, name, usgsid, state, county, map, elevation, location
         from peaks
         where id = $id""".query[PeakView]

  def findQuery(minElev: Int): Query0[PeakView] =
    sql"""
          select id, name, usgsid, state, county, map, elevation, location
          from peaks
          where elevation > $minElev""".query[PeakView]

  // TODO: unique constraint for usgsid
  def insertQuery(newPeak: Peak): Update0 = // ConnectionIO[Throwable \/ PeakView] =
    sql"""
    insert into peaks (name, usgsid, state, county, map, elevation, location)
    values (${newPeak.name}, ${newPeak.usgsid}, ${newPeak.state},
            ${newPeak.county}, ${newPeak.map}, ${newPeak.elevation},
            ST_SetSRID(${newPeak.location}::GEOMETRY, 4326))
    """.update
}
