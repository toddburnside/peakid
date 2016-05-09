package dao

import doobie.imports._
import scalaz.concurrent.Task

/**
 * Created by tburnside on 5/8/2016.
 */
trait DoobieTransactor {
  val xa: Transactor[Task]
}
