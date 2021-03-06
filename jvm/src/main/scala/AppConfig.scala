package peakid

import classy.config._
import classy.DecodeError
import classy.generic._
import com.typesafe.config.{Config, ConfigFactory}

object AppConfig {
  def load(): Either[DecodeError, AppConfig] = {
    val appConfigDecoder = deriveDecoder[Config, AppConfig]

    val config: Config = ConfigFactory.load()
    appConfigDecoder(config)
  }
}

sealed trait ElevationProvider
case class GoogleApi() extends ElevationProvider
case class NationalMaps() extends ElevationProvider

case class DB(driver: String, url: String, user: String, pass: String)
case class Google(key: String)
case class ServerConfig(port: Int, host: String)
case class AppConfig(db: DB,
                     google: Google,
                     elevationProvider: ElevationProvider,
                     server: ServerConfig)
