package geography

import models.Location
import models.PeakBase.NewPeak
import org.specs2.matcher.ResultMatchers
import org.specs2.mutable.Specification

// TODO: Add tests for crossing 0, 180, over poles, etc.
object FlatGeographySpec extends Specification with ResultMatchers {
  val loc1 = Location(-5.0, 50.0)
  val loc2 = Location(-3.0, 58.0)
  val peak1 = new NewPeak((),
                          "Mountain",
                          1,
                          "OR",
                          "Clackamas",
                          "Map",
                          7000,
                          Location(-5.0, 50.0))
  val peak2 = new NewPeak((),
                          "Mountain",
                          1,
                          "OR",
                          "Clackamas",
                          "Map",
                          6000,
                          Location(-3.0, 58.0))
  "Test FlatGeography" >> {
    "Test simple distance between Locations" >> {
      loc1.distanceTo(loc2) must beCloseTo(899.01 +/- 0.1)
    }
    "Test simple bearing between Locations" >> {
      loc1.bearingTo(loc2).toInt must_== 7
    }
    "Test simple destination from Locations" >> {
      val (lon, lat) = loc1.destination(50.0, 45.0)
      lon must beCloseTo(-4.502 +/- 0.001)
      lat must beCloseTo(50.317 +/- 0.001)
    }
    "Test distance between Peaks" >> {
      peak1.distanceTo(peak2) must beCloseTo(899.01 +/- 0.1)
    }
    "Test distance between Location and Peak" >> {
      loc1.distanceTo(peak2) must beCloseTo(899.01 +/- 0.1)
    }
    "Test distance between Peak and Location" >> {
      peak1.distanceTo(loc2) must beCloseTo(899.01 +/- 0.1)
    }
  }
}
