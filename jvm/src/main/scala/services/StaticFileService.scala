package services

import java.io.File

import fs2.Task
import org.http4s.{HttpService, StaticFile}
import org.http4s.dsl._

object StaticFileService {
  val service = HttpService {
    case request @ GET -> Root / "public" / path
        if List(".js", ".js.map").exists(path.endsWith) =>
      StaticFile
        .fromFile(new File(s"js/target/scala-2.12/$path"), Some(request))
        .map(Task.now)
        .getOrElse(NotFound())
    case request @ GET -> Root =>
      StaticFile
        .fromResource("/index.html", Some(request))
        .map(Task.now)
        .getOrElse(NotFound())
  }
}
