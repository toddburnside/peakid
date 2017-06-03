package free

import cats.data.Coproduct
import cats.~>
import doobie.util.transactor.Transactor
import elevation.ElevationProvider
import free.freeElevation.Elevation
import free.peakData.PeakData
import fs2.Task

object App {
  type PeakIdApp[A] = Coproduct[PeakData, Elevation, A]
  def interpreter(xa: Transactor[Task], elevProvider: ElevationProvider): PeakIdApp ~> Task =
    free.peakData.doobiePeakDataInterpreter(xa) or
      free.freeElevation.googleElevationInterpreter(elevProvider)
}
