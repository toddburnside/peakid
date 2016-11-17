package models

import geography.{ElevatedGeography, FlatGeography}
import io.circe.Decoder
import io.circe.generic.auto._

//case class PeakId(id: Int)

case class PeakBase[T](id: T, name: String, usgsid: Int, state: String, county: String,
                map: String, elevation: Int, location: Location)

//case class PeakView(id: PeakId, peak: Peak)

object PeakBase {
  type PeakId = Int
  type NewPeak = PeakBase[Unit]
  type Peak = PeakBase[PeakId]

  // use a circe 'incomplete' decoder to deal with the 'missing' Unit id.
  implicit val decodeNewPeak: Decoder[NewPeak] = Decoder[Unit => NewPeak].map(u => u(()))

  // TODO: Move this elsewhere?
  implicit def elevationGeographyPeak[A](a: PeakBase[A]): ElevatedGeography[PeakBase[A]] =
  new ElevatedGeography[PeakBase[A]] {
    override val lat: Double = a.location.lat
    override val lon: Double = a.location.lon
    override val elevation: Int = a.elevation
  }
}