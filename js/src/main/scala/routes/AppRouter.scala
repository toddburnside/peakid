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
        ^.className := "container",
        <.div(
          ^.className := "row",
          <.div(^.className := "col-md-2",
                criteriaConnection(
                  c => locationConnection(l => SearchCriteriaEntry(c, l)))),
          <.div(^.className := "col-md-10", r.render())
        )
      )
    )
  }

  val config = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._

    val peakConnection = AppCircuit.connect(_.visiblePeaks)

    (trimSlashes
      | staticRoute(root, PeakMapPage) ~> render(PeakMap())
      | staticRoute("#list", PeakListPage) ~> render(
        peakConnection(p => PeakList(p))))
      .notFound(redirectToPage(PeakMapPage)(Redirect.Replace))
      .renderWith(layout)
  }

  val baseUrl = BaseUrl.fromWindowOrigin

  var router = Router(baseUrl, config)
}
