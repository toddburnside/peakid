package free

import cats.free.{Free, Inject}
import cats.~>
import doobie.util.transactor.Transactor
import models.PeakBase.{NewPeak, Peak}
import fs2.{Stream, Task}
import doobie.imports._
import repositories.PeakRepositoryDb

object peakData {

  sealed trait PeakData[A]
  case class FindPeaks[F[_]](minElev: Int) extends PeakData[Stream[F, Peak]]
  case class FindOnePeak(id: Int) extends PeakData[Either[Throwable, Option[Peak]]]
  case class InsertPeak(newPeak: NewPeak) extends PeakData[Either[Throwable, Peak]]

  class PeakDatas[F[_]](implicit I: Inject[PeakData, F]) {
    def findPeaks[G[_]](minElev: Int): Free[F, Stream[G, Peak]] = Free.inject[PeakData, F](FindPeaks(minElev))
    def findOnePeak(id: Int): Free[F, Either[Throwable, Option[Peak]]] = Free.inject[PeakData, F](FindOnePeak(id))
    def insertPeak(newPeak: NewPeak): Free[F, Either[Throwable, Peak]] = Free.inject[PeakData, F](InsertPeak(newPeak))
  }

  object PeakDatas {
    implicit def peakDatas[F[_]](implicit I: Inject[PeakData, F]): PeakDatas[F] = new PeakDatas[F]
  }

  def doobiePeakDataInterpreter(xa: Transactor[Task]): PeakData ~> Task =
    new (PeakData ~> Task) {
      val peakRepo = new PeakRepositoryDb(xa)
    override def apply[A](fa: PeakData[A]): Task[A] = fa match {
      case FindPeaks(minElev) => Task.delay(peakRepo.find(minElev))
      case FindOnePeak(id) => peakRepo.findOne(id)
      case InsertPeak(newPeak) => peakRepo.insert(newPeak)
    }
  }
}