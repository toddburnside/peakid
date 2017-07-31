import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.scalajs.dom
import routes.AppRouter
import styles.AppStyles

import scalacss.DevDefaults._
import scalajs.react.components.{Pager, ReactTable}

@JSExportTopLevel("hReactApp")
object ReactApp extends JSApp {

  @JSExport
  def main(): Unit = {
    // TODO: Use GlobalRegistry for CSS
    AppStyles.addToDocument()
    ReactTable.DefaultStyle.addToDocument()
    Pager.DefaultStyle.addToDocument()
    AppRouter.router().renderIntoDOM(dom.document.getElementById("app"))
    () // explicitly return unit to avoid discarded value compiler warning for above line.
  }
}
