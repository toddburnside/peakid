package components

import diode.data.{Pot, PotState}
import diode.react.ModelProxy
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillReceiveProps
import japgolly.scalajs.react.vdom.html_<^._
import models.Location
import services.{
  GetCurrentLocation,
  RequestPeaks,
  SearchCriteria,
  UpdateSearchCriteria
}

object SearchCriteriaEntry {

  case class Props(criteriaProxy: ModelProxy[SearchCriteria],
                   locationProxy: ModelProxy[Pot[Location]])
  case class State(searchCriteria: SearchCriteria, lastLocationState: PotState)

  class Backend($ : BackendScope[Props, State]) {

    def updateLon(e: ReactEventFromInput) = {
      val value = e.target.value.toDouble
      $.modState(s =>
        s.copy(searchCriteria = s.searchCriteria.copy(lon = value)))
    }

    def updateLat(e: ReactEventFromInput) = {
      val value = e.target.value.toDouble
      $.modState(s =>
        s.copy(searchCriteria = s.searchCriteria.copy(lat = value)))
    }

    def updateElev(e: ReactEventFromInput) = {
      val value = e.target.value.toInt
      $.modState(s =>
        s.copy(searchCriteria = s.searchCriteria.copy(minElev = value)))
    }

    def submit(e: ReactEventFromInput): Callback = {
      for {
        _ <- e.preventDefaultCB
        props <- $.props
        state <- $.state
        _ <- props.criteriaProxy.dispatchCB(
          UpdateSearchCriteria(state.searchCriteria))
        _ <- props.criteriaProxy.dispatchCB(RequestPeaks())
      } yield ()
    }

    def getCurrentLocation: Callback = {
      $.props >>= (_.locationProxy.dispatchCB(GetCurrentLocation()))
    }

    // TODO: Need to handle failure to get current location better, and use current location
    def createForm(s: State) = {
      <.form(
        ^.onSubmit ==> submit,
        ^.className := "ui form",
        <.div(
          ^.className := "field",
          <.label(^.`for` := "longitude", "Longitude"),
          <.input.number(^.id := "longitude",
                         ^.value := s.searchCriteria.lon.toString,
                         ^.onChange ==> updateLon)
        ),
        <.div(
          ^.className := "field",
          <.label(^.`for` := "latitude", "Latitude"),
          <.input.number(^.id := "latitude",
                         ^.value := s.searchCriteria.lat.toString,
                         ^.onChange ==> updateLat)
        ),
        <.div(
          ^.className := "field",
          <.label(^.`for` := "minElev", "Minimum Elevation"),
          <.input.number(^.id := "minElev",
                         ^.value := s.searchCriteria.minElev.toString,
                         ^.onChange ==> updateElev)
        ),
        <.button(^.className := "ui primary button", "Load Peaks"),
        <.button(^.className := "ui button",
                 ^.onClick --> getCurrentLocation,
                 "Get Current Location")
      )
    }

    def render(props: Props, state: State) = {
      <.div(
        props
          .locationProxy()
          .renderPending(
            _ > 500,
            _ => <.div(<.i(^.className := "huge spinner loading icon"))),
        props
          .locationProxy()
          .renderFailed(_ => <.div("Error getting current location")),
        createForm(state)
      )
    }
  }

  def willReceiveProps(f: ComponentWillReceiveProps[Props, State, Backend]) = {
    val lastState = f.state.lastLocationState
    val potLoc = f.nextProps.locationProxy()
    val nextState = potLoc.state
    // if the state was pending and is not ready, that means we are receiving
    // a new "current" location, so we need to update the lon and lat.
    Callback.when(lastState == PotState.PotPending && potLoc.isReady)(
      potLoc.fold(Callback.empty)(loc =>
        f.modState(s =>
          s.copy(searchCriteria =
            s.searchCriteria.copy(lon = loc.lon, lat = loc.lat))))) >>
      // if the state has changed, update the last state.
      Callback.when(lastState != nextState)(f.modState(s =>
        s.copy(lastLocationState = nextState)))
  }

  val component = ScalaComponent
    .builder[Props]("SearchCriteriaEntry")
    .initialStateFromProps(p =>
      State(p.criteriaProxy(), p.locationProxy().state))
    .renderBackend[Backend]
    .componentWillReceiveProps(willReceiveProps)
    .build

  def apply(criteriaProxy: ModelProxy[SearchCriteria],
            locationProxy: ModelProxy[Pot[Location]]): VdomElement =
    component(Props(criteriaProxy, locationProxy))
}
