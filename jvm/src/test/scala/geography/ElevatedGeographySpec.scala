package geography

import models.PeakBase.NewPeak
import models.Location
import org.specs2.matcher.ResultMatchers
import org.specs2.mutable.Specification

object ElevatedGeographySpec extends Specification with ResultMatchers {
  val peak1 = new NewPeak((), "name", 0, "OR", "Clackamas", "here", 7000, Location(0.0, 0.0))

  "Test ElevationGeography" >> {
    "Distance to horizon at 7000m" >> {
      peak1.toHorizon must beCloseTo(323.0 +/- 0.1)
    }
  }
}
