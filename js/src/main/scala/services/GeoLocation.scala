package services

import models.Location
import org.scalajs.dom

import scala.concurrent.{Future, Promise}

object GeoLocation {

  def getCurrentLocation(): Future[Location] = {
    val geo = dom.window.navigator.geolocation
    val p = Promise[Location]()

    def roundTo5(d: Double) = (d * 10000).round / 10000.toDouble

    geo.getCurrentPosition(pos =>
                             p.success(
                               Location(roundTo5(pos.coords.longitude),
                                        roundTo5(pos.coords.latitude))),
                           err => p.failure(new Exception(err.message)))

    p.future
  }
}
