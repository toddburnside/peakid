package models

case class PeakId(id: Long)

case class Peak(name: String, usgsid: Int, state: String, county: String,
                map: String, elevation: Int, location: Location)

case class PeakView(id: PeakId, peak: Peak)