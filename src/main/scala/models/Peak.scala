package models

case class Peak(name: String, usgsid: Int, state: String, county: String,
                map: String, elevation: Int, location: Location)
