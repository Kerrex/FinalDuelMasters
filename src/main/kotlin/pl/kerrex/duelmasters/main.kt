package pl.kerrex.duelmasters

import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.server.engine.*
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import org.kodein.di.generic.instance

fun main(args: Array<String>) {
    initContext()
    embeddedServer(Netty, 8080) { app() }
            .start(wait = true)
}

fun Application.app() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(CallLogging)

    routing {
        post("/register") {
            val handler: VerifyRequestHandler by Kodein.global.instance()
            handler.handleRequest(call)
        }
    }
}