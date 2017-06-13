package components

import diode.data.{Pot, PotState}
import diode.react.ModelProxy
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^._
import models.Location
import services.{GetCurrentLocation, RequestPeaks, SearchCriteria, UpdateSearchCriteria}

object SearchCriteriaEntry {

  case class Props(criteriaProxy: ModelProxy[SearchCriteria], locationProxy: ModelProxy[Pot[Location]])
  case class State(searchCriteria: SearchCriteria, lastLocationState: PotState)

  class Backend($: BackendScope[Props, State]) {

    // TODO: Make this work
    // Ughh. I need to bring in a lens library...
    def willUpdate: Callback = {
      for {
        props <- $.props
        state <- $.state
        pot = props.locationProxy()
        oldState = state.lastLocationState
        _ = if (oldState == PotState.PotPending && pot.isReady)
            $.modState(s => s.copy(searchCriteria = s.searchCriteria.copy(lon = 123.45)))
        _ = if (oldState != pot.state)
            $.modState(s => s.copy(lastLocationState = props.locationProxy().state))
      } yield ()
    }

    def updateLon(e: ReactEventFromInput) = {
      val value = e.target.value.toDouble
      $.modState(s => s.copy(searchCriteria = s.searchCriteria.copy(lon = value)))
    }

    def updateLat(e: ReactEventFromInput) = {
      val value = e.target.value.toDouble
      $.modState(s => s.copy(searchCriteria = s.searchCriteria.copy(lat = value)))
    }

    def updateElev(e: ReactEventFromInput) = {
      val value = e.target.value.toInt
      $.modState(s => s.copy(searchCriteria = s.searchCriteria.copy(minElev = value)))
    }

    def submit: Callback = {
      for {
        props <- $.props
        state <- $.state
        _ <- props.criteriaProxy.dispatchCB(UpdateSearchCriteria(state.searchCriteria))
        _ <- props.criteriaProxy.dispatchCB(RequestPeaks())
      } yield ()
    }

    def getCurrentLocation: Callback = {
      $.props >>= (_.locationProxy.dispatchCB(GetCurrentLocation()))
    }

    // TODO: Need to handle failure to get current location better, and use current location
    def createForm(s: State) = {
      <.div(^.className := "form-inline",
        <.div(^.className := "form-group",
          <.label(^.`for` := "longitude", "Longitude"),
          <.input.number(^.className := "form-control", ^.id := "longitude",
            ^.value := s.searchCriteria.lon.toString, ^.onChange ==> updateLon)
        ),
        <.div(^.className := "form-group",
          <.label(^.`for` := "latitude", "Latitude"),
          <.input.number(^.className := "form-control", ^.id := "latitude",
            ^.value := s.searchCriteria.lat.toString, ^.onChange ==> updateLat)
        ),
        <.div(^.className := "form-group",
          <.label(^.`for` := "minElev", "Minimum Elevation"),
          <.input.number(^.className := "form-control", ^.id := "minElev",
            ^.value := s.searchCriteria.minElev.toString, ^.onChange ==> updateElev)
        ),
        <.button(^.className := "btn btn-default", ^.onClick --> submit, "Update"),
        <.button(^.className := "btn btn-default", ^.onClick --> getCurrentLocation, "Get Current Location")
      )
    }

    def render(props: Props, state: State) = {
      <.div("Current Location - will be Visible Peaks Search Criteria form   ",
        props.locationProxy().renderPending(_ > 500,  _ =>
          <.div(<.i(^.className := s"fa fa-spinner fa-pulse fa-3x fa-fw"))),
        props.locationProxy().renderFailed(_ => <.div("Error getting current location")),
        props.locationProxy().render(l => <.div(s"Current Location: (${l.lon}, ${l.lat})")),
        <.div(s"Search coordinates (${props.criteriaProxy().lon}, ${props.criteriaProxy().lat}): ${props.criteriaProxy().minElev}"),
        createForm(state)
      )
    }
  }

  val component = ScalaComponent.builder[Props]("SearchCriteriaEntry")
    .initialStateFromProps(p => State(p.criteriaProxy(), p.locationProxy().state))
    .renderBackend[Backend]
    .componentWillUpdate(_.backend.willUpdate)
    .build

  def apply(criteriaProxy: ModelProxy[SearchCriteria], locationProxy: ModelProxy[Pot[Location]]): VdomElement =
    component(Props(criteriaProxy, locationProxy))
}
