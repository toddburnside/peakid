package components

import diode.react.ModelProxy
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import models.VisiblePeak
import services.VisiblePeaks

import scalajs.react.components.GoogleMap
import scalajs.react.components.fascades._

object PeakMap {
  case class Props(visiblePeaksProxy: ModelProxy[VisiblePeaks])

  def makeContent(peak: VisiblePeak) =
    f"<h4>${peak.name}</h4><div>Elevation: ${peak.elevation}'</div><div>Distance: ${peak.distance}%.1f km</div>" +
      f"<div>Bearing: ${peak.bearing}%.0f&deg;</div><div>Latitude: ${peak.location.lat}%.5f&deg;</div>" +
      f"<div>Longitude: ${peak.location.lon}%.5f&deg;</div>"

  def hereMarker(lat: Double, lon: Double): Marker = {
    val image = Icon(
      url =
        "https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png",
      size = Size(20, 32),
      origin = Point(0, 0),
      anchor = Point(0, 32)
    )
    Marker(position = LatLng(lat, lon), title = "You are here!", icon = image)
  }

  def render(props: Props) = {
    val potPeaks = props.visiblePeaksProxy.value.peaks
    val criteria = props.visiblePeaksProxy.value.searchCriteria

    <.div(
      potPeaks.renderEmpty(<.h1("Peaks have not been loaded")),
      potPeaks.renderPending(
        _ > 500,
        _ => <.div(<.i(^.className := s"fa fa-spinner fa-pulse fa-3x fa-fw"))),
      potPeaks.render(peaks => {
        val markers = peaks
          .map(
            p =>
              Marker(position = LatLng(p.location.lat, p.location.lon),
                     title = p.name,
                     content = makeContent(p)))
          .toList
        val here = hereMarker(criteria.lat, criteria.lon)
        <.div(Header(criteria),
              GoogleMap(center = LatLng(criteria.lat, criteria.lon),
                        zoom = 6,
                        markers = here :: markers,
                        width = "100%"))
      })
    )
  }

  val component = ScalaComponent
    .builder[Props]("PeakMapPage")
    .render_P(render)
    .build

  def apply(peaksProxy: ModelProxy[VisiblePeaks]): VdomElement =
    component(Props(peaksProxy))
}
