package services

import models.Location
import org.scalajs.dom

import scala.concurrent.{Future, Promise}

object GeoLocation {

  def getCurrentLocation(): Future[Location] = {
    val geo = dom.window.navigator.geolocation
    val p = Promise[Location]()

    geo.getCurrentPosition(
      pos => p.success(Location(pos.coords.longitude, pos.coords.latitude)),
      err => p.failure(new Exception(err.message)))

    p.future
  }
}
