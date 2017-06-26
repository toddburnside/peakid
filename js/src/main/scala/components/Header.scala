package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import services.SearchCriteria

object Header {
  val component = ScalaComponent
    .builder[SearchCriteria]("Header")
    .render_P(criteria => {
      <.div(
        <.h2(s"Peaks visible from (${criteria.lon}, ${criteria.lat})"),
        <.h3(s"Minimum elevation of peaks is ${criteria.minElev}'")
          .when(criteria.minElev > 0)
      )
    })
    .build

  def apply(criteria: SearchCriteria): VdomElement = component(criteria)
}
