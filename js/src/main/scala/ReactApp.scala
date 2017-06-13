import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.scalajs.dom
import routes.AppRouter
import services.{AppCircuit, GetCurrentLocation, RequestPeaks}
import styles.AppStyles

import scalacss.DevDefaults._

@JSExportTopLevel("hReactApp")
object ReactApp extends JSApp {

  @JSExport
  def main(): Unit = {
    AppStyles.addToDocument()
//    AppCircuit.dispatch(RequestPeaks())
//    AppCircuit.dispatch(GetCurrentLocation())
    AppRouter.router().renderIntoDOM(dom.document.getElementById("app"))
  }
}
