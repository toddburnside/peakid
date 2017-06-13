package components

import diode.data.Pot
import diode.react.ModelProxy
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import models.VisiblePeak

object PeakList {

  case class Props(visiblePeaksProxy: ModelProxy[Pot[Seq[VisiblePeak]]])

  class Backend($: BackendScope[Props, Unit]) {
    def createTable(peaks: Seq[VisiblePeak]) =
      if (peaks.length == 0) <.div("No peaks to display")
      else {
        <.table(
          <.thead(
            <.tr(
              <.th("Name"), <.th("Elevation"), <.th("Distance"), <.th("Bearing"), <.th("Longitude"), <.th("Latitude")
            )
          ),
          <.tbody(
            peaks.toTagMod(peak =>
              <.tr(
                <.td(peak.name), <.td(peak.elevation), <.td(f"${peak.distance}%.1f"), <.td(Math.round(peak.bearing)),
                <.td(f"${peak.location.lon}%.2f"), <.td(f"${peak.location.lat}%.2f")
              )
            )
          )
        )
      }

    def render(props: Props) = {
      <.div(
        <.h1("List of visible peaks"),
        props.visiblePeaksProxy().renderEmpty(<.div("Peaks have not been loaded")),
        props.visiblePeaksProxy().renderPending(_ > 500, _ =>
          <.div(<.i(^.className := s"fa fa-spinner fa-pulse fa-3x fa-fw"))),
        props.visiblePeaksProxy().renderFailed(_ => <.div("Error Loading Peaks")),
        props.visiblePeaksProxy().render(p => createTable(p))
      )
    }
  }

  val component = ScalaComponent.builder[Props]("PeakListPage")
    .renderBackend[Backend]
    .build

  def apply(visiblePeaksProxy: ModelProxy[Pot[Seq[VisiblePeak]]]): VdomElement = component(Props(visiblePeaksProxy))
}
