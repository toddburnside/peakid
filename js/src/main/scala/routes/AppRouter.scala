package routes

import components.{Navbar, PeakList, PeakMap, SearchCriteriaEntry}
import japgolly.scalajs.react.extra.router.{
  BaseUrl,
  Redirect,
  Resolution,
  Router,
  RouterConfigDsl,
  RouterCtl
}
import japgolly.scalajs.react.vdom.html_<^._
import services.AppCircuit

object AppRouter {

  sealed trait AppPage

  case object PeakListPage extends AppPage
  case object PeakMapPage extends AppPage

  def layout(c: RouterCtl[AppPage], r: Resolution[AppPage]) = {
    val locationConnection = AppCircuit.connect(_.currentLocation)
    val criteriaConnection = AppCircuit.connect(_.visiblePeaks.searchCriteria)

    <.div(
      Navbar(c, r.page),
      <.div(
        ^.className := "ui grid container",
        <.div(
          ^.className := "sixteen wide mobile five wide tablet three wide computer column",
          criteriaConnection(
            c => locationConnection(l => SearchCriteriaEntry(c, l)))),
        <.div(
          ^.className := "sixteen wide mobile eleven wide tablet ten wide computer column",
          r.render())
      )
    )
  }

  val config = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._

    val peakConnection = AppCircuit.connect(_.visiblePeaks)

    (trimSlashes
      | staticRoute("#map", PeakMapPage) ~> render(
        peakConnection(p => PeakMap(p)))
      | staticRoute("#list", PeakListPage) ~> render(
        peakConnection(p => PeakList(p))))
      .notFound(redirectToPage(PeakMapPage)(Redirect.Replace))
      .renderWith(layout)
  }

  val baseUrl = BaseUrl.fromWindowOrigin

  var router = Router(baseUrl, config)
}
