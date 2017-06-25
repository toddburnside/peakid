package components

import diode.data.Pot
import diode.react.ModelProxy
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import models.VisiblePeak
import services.VisiblePeaks

object PeakList {

  case class Props(visiblePeaksProxy: ModelProxy[VisiblePeaks])

  class Backend($ : BackendScope[Props, Unit]) {
    def createTable(peaks: Seq[VisiblePeak]) =
      if (peaks.length == 0) <.div("No peaks to display")
      else {
        <.table(
          <.thead(
            <.tr(
              <.th("Name"),
              <.th("Elevation"),
              <.th("Distance"),
              <.th("Bearing"),
              <.th("Longitude"),
              <.th("Latitude")
            )
          ),
          <.tbody(
            peaks.toTagMod(
              peak =>
                <.tr(
                  <.td(peak.name),
                  <.td(peak.elevation),
                  <.td(f"${peak.distance}%.1f"),
                  <.td(Math.round(peak.bearing)),
                  <.td(f"${peak.location.lon}%.2f"),
                  <.td(f"${peak.location.lat}%.2f")
              ))
          )
        )
      }

    def render(props: Props) = {
      val criteria = props.visiblePeaksProxy.value.searchCriteria
      val potPeaks = props.visiblePeaksProxy.value.peaks
      <.div(
        potPeaks
          .renderEmpty(<.h1("Peaks have not been loaded")),
        potPeaks
          .renderPending(
            _ > 500,
            _ =>
              <.div(
                <.i(^.className := s"fa fa-spinner fa-pulse fa-3x fa-fw"))),
        potPeaks
          .renderFailed(_ => <.div("Error Loading Peaks")),
        potPeaks
          .render(p => {
            <.div(<.h1(s"Peaks visible from (${criteria.lon}, ${criteria.lat}) at least ${criteria.minElev} feet high"), createTable(p))
          })
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("PeakListPage")
    .renderBackend[Backend]
    .build

  def apply(
      visiblePeaksProxy: ModelProxy[VisiblePeaks]): VdomElement =
    component(Props(visiblePeaksProxy))
}
