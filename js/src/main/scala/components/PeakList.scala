package components

import diode.data.Pot
import diode.react.ModelProxy
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import models.VisiblePeak
import services.VisiblePeaks

import scalajs.react.components.ReactTable

object PeakList {

  case class Props(visiblePeaksProxy: ModelProxy[VisiblePeaks])

  class Backend($ : BackendScope[Props, Unit]) {
    val columns =
      List("name", "elevation", "distance", "bearing", "longitude", "latitude")

    def formatDouble(p: Int)(a: Any): VdomElement = {
      val d = a.asInstanceOf[Double]
      val s = s"%.${p}f".format(d)
      <.span(s)
    }
    //config is a List of touple4 (String, Option[(Any) => ReactElement], Option[(Model, Model) => Boolean],Option[Double])
    // ._1: columnname you want to config
    // ._2: custom render function (custom cell factory)
    // ._3: Sorting function
    // ._4: column width (flex := width)
    val config: List[ReactTable.Config] = List(
      ("name", None, Some(ReactTable.getStringSort("name")), None),
      ("elevation", None, Some(ReactTable.getIntSort("elevation")), None),
      ("distance",
       Some(formatDouble(1)),
       Some(ReactTable.getDoubleSort("distance")),
       None),
      ("bearing",
       Some(formatDouble(0)),
       Some(ReactTable.getDoubleSort("bearing")),
       None),
      ("longitude", Some(formatDouble(5)), None, None),
      ("latitude", Some(formatDouble(5)), None, None)
    )
    def peakToMap(peak: VisiblePeak): ReactTable.Model = {
      Map(
        "name" -> peak.name,
        "elevation" -> peak.elevation,
        "distance" -> peak.distance,
        "bearing" -> peak.bearing,
        "longitude" -> peak.location.lon,
        "latitude" -> peak.location.lat
      )
    }

    def peaksToMapVector(peaks: Seq[VisiblePeak]): Vector[ReactTable.Model] =
      peaks.map(peakToMap).toVector

    def render(props: Props) = {
      val criteria = props.visiblePeaksProxy.value.searchCriteria
      val potPeaks = props.visiblePeaksProxy.value.peaks
      <.div(
        potPeaks
          .renderEmpty(<.h1("Peaks have not been loaded")),
        potPeaks
          .renderPending(
            _ > 500,
            _ => <.div(<.i(^.className := "massive spinner loading icon"))),
        potPeaks
          .renderFailed(_ => <.div("Error Loading Peaks")),
        potPeaks
          .render(p => {
            val data = peaksToMapVector(p)
            <.div(
              Header(criteria),
              ReactTable(data = data,
                         columns = columns,
                         config = config,
                         rowsPerPage = 15).when(p.nonEmpty),
              <.div("No peaks to display").when(p.isEmpty)
            )
          })
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("PeakListPage")
    .renderBackend[Backend]
    .build

  def apply(visiblePeaksProxy: ModelProxy[VisiblePeaks]): VdomElement =
    component(Props(visiblePeaksProxy))
}
