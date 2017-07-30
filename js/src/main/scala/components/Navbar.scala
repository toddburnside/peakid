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
    MenuItem(1, _ => "Map", "marker", PeakMapPage),
    MenuItem(2, _ => "List", "unordered list", PeakListPage)
  )

  def nameToIcon(iconName: String) = <.i(^.className := s"$iconName icon")

  def renderMenuItem(props: Props)(item: MenuItem): VdomElement =
    <.a(
      ^.className := "item",
      (^.className := "active").when(props.currentPage == item.page),
      ^.href := props.router.urlFor(item.page).value,
      nameToIcon(item.iconName),
      item.label(props)
    )

  private class Backend($ : BackendScope[Props, Unit]) {
    def render(props: Props) = {
      <.div(
        ^.className := "ui fixed inverted menu",
        <.div(
          ^.className := "ui container",
          <.div(^.className := "header item", "peakid"),
          menuItems.toTagMod(renderMenuItem(props) _),
          <.div(^.className := "right menu"),
          <.a(^.className := "item",
              ^.href := "https://github.com/toddburnside/peakid",
              nameToIcon("github"),
              "Source Code")
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
