package peakid

import java.io.File

import classy.config._
import classy.core.DecodeError
import classy.generic._
import com.typesafe.config.{Config, ConfigFactory}

object AppConfig {
  def load(): Either[DecodeError, AppConfig] = {
    val appConfigDecoder = deriveDecoder[Config, AppConfig]

    val config: Config = ConfigFactory.parseFile(new File("application.conf"))
    appConfigDecoder(config)
  }
}

case class DB(driver: String, url: String, user: String, pass: String)
case class Google(key: String)
case class AppConfig(db: DB, google: Google)
