package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object PeakMap {

  val component = ScalaComponent.builder
    .static("PeakListPage")(<.div("A map of visible peaks"))
    .build

  def apply(): VdomElement = component().vdomElement
}