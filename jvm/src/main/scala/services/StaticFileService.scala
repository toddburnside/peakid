package services

import java.io.File

import fs2.Task
import org.http4s.{HttpService, StaticFile}
import org.http4s.dsl._

object StaticFileService {
  val service = HttpService {
    case request @ GET -> Root =>
//      StaticFile.fromFile(new File("index.html"), Some(request)).map(Task.now).getOrElse(BadRequest())
      StaticFile.fromResource("/index.html", Some(request)).map(Task.now).getOrElse(BadRequest())
  }
}
