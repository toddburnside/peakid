package services

import java.io.File

import cats.effect.Effect
import org.http4s.{HttpService, StaticFile}
import org.http4s.dsl._

class StaticFileService[F[_]: Effect] extends Http4sDsl[F] {
  val service: HttpService[F] = HttpService[F] {
    case request @ GET -> Root / "public" / path
        if List(".js", ".js.map").exists(path.endsWith) =>
      StaticFile
        .fromFile(new File(s"js/target/scala-2.12/$path"), Some(request))
        .getOrElseF(NotFound())
    case request @ GET -> Root =>
      StaticFile
        .fromResource("/index.html", Some(request))
        .getOrElseF(NotFound())
  }
}
