package pl.riscosoftware

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig

class Configuration {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    fun getProperty(name: String): String? {
        return config.propertyOrNull(name)?.getString()
    }
}