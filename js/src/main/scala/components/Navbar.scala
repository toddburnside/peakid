package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import routes.AppRouter.{AppPage, PeakListPage, PeakMapPage}

object Navbar {

  case class Props(router: RouterCtl[AppPage], currentPage: AppPage)

  // TODO: Could make icons typesafe as in the scalajs-spa-tutorial by Otto Chrons.
  private case class MenuItem(idx: Int,
                              label: (Props) => VdomNode,
                              iconName: String,
                              page: AppPage)
  private val menuItems = Seq(
    MenuItem(1, _ => "Map", "map-marker", PeakMapPage),
    MenuItem(2, _ => "List", "list", PeakListPage)
  )

  def nameToIcon(iconName: String) = <.i(^.className := s"fa fa-$iconName")

  def renderMenuItem(props: Props)(item: MenuItem): VdomElement =
    <.li(
      ^.key := item.idx,
      (^.className := "active").when(props.currentPage == item.page),
      props.router
        .link(item.page)(nameToIcon(item.iconName), " ", item.label(props))
    )

  private class Backend($ : BackendScope[Props, Unit]) {
    def render(props: Props) = {
      <.nav(
        ^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(
          ^.className := "container",
          <.div(^.className := "navbar-header",
                <.span(^.className := "navbar-brand", "peakid")),
          <.div(^.className := "collapse navbar-collapse",
                <.ul(^.className := "navbar navbar-nav",
                     menuItems.toTagMod(renderMenuItem(props) _)))
        )
      )
    }
  }

  private val component = ScalaComponent
    .builder[Props]("Navbar")
    .renderBackend[Backend]
    .build

  def apply(ctl: RouterCtl[AppPage], currentPage: AppPage): VdomElement =
    component(Props(ctl, currentPage))
}
