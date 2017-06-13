package services

import diode.{Action, ActionHandler, ActionResult, Circuit}
import diode.data.PotState._
import diode.data.{Empty, Pot, PotAction}
import diode.react.ReactConnector
import models.{Location, VisiblePeak}
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

// The application model
case class SearchCriteria(lon: Double, lat: Double, minElev: Int)
case class VisiblePeaks(searchCriteria: SearchCriteria, peaks: Pot[Seq[VisiblePeak]])

case class RootModel(visiblePeaks: VisiblePeaks, currentLocation: Pot[Location])

// Actions
case class UpdateSearchCriteria(searchCriteria: SearchCriteria) extends Action

case class RequestPeaks(potResult: Pot[Seq[VisiblePeak]] = Empty) extends PotAction[Seq[VisiblePeak], RequestPeaks] {
  override def next(newResult: Pot[Seq[VisiblePeak]]): RequestPeaks = RequestPeaks(newResult)
}

case class GetCurrentLocation(potResult: Pot[Location] = Empty)
  extends PotAction[Location, GetCurrentLocation] {
  override def next(newResult: Pot[Location]): GetCurrentLocation = GetCurrentLocation(newResult)
}

object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  override def initialModel: RootModel = RootModel(VisiblePeaks(SearchCriteria(-122.6, 45.5, 14000), Empty), Empty)

  def loadPeaks(lon: Double, lat: Double, minElev: Int): Future[Seq[VisiblePeak]] = {
    import io.circe.parser.decode
    import io.circe.generic.auto._
    def decodePeaks(s: String): Future[Seq[VisiblePeak]] =
      decode[Seq[VisiblePeak]](s).fold(Future.failed(_), Future.successful(_))

    Ajax.get(s"api/peaks?lon=$lon&lat=$lat&minElev=$minElev")
      .flatMap(xhr => decodePeaks(xhr.responseText))
  }

  val peaksHandler = new ActionHandler(zoomTo(_.visiblePeaks)) {
    override protected def handle = {
      case action: RequestPeaks =>
        val updateEffect = action.effect(
          loadPeaks(value.searchCriteria.lon, value.searchCriteria.lat, value.searchCriteria.minElev)
        )(peaks => peaks)

        action.handle {
          case PotEmpty =>
            updated(value.copy(peaks = value.peaks.pending()), updateEffect)
          case PotPending =>
            noChange
          case PotReady =>
            updated(value.copy(peaks = action.potResult))
          case PotUnavailable =>
            updated(value.copy(peaks = value.peaks.unavailable()))
          case PotFailed =>
            val ex = action.result.failed.get
            updated(value.copy(peaks = value.peaks.fail(ex)))
        }
    }
  }

  val currentLocationHandler = new ActionHandler(zoomTo(_.currentLocation)) {
    override protected def handle = {
      case action: GetCurrentLocation =>
        val locationEffect = action.effect[Location](GeoLocation.getCurrentLocation())(loc => loc)

        action.handleWith(this, locationEffect)(PotAction.handler())
    }
  }

  val searchCriteriaHandler = new ActionHandler(zoomTo(_.visiblePeaks.searchCriteria)) {
    override protected def handle = {
      case UpdateSearchCriteria(searchCriteria) => updated(searchCriteria)
    }
  }

  override protected def actionHandler: AppCircuit.HandlerFunction =
    composeHandlers(peaksHandler, currentLocationHandler, searchCriteriaHandler)
}
