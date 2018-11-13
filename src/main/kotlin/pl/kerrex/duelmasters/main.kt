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
import pl.kerrex.duelmasters.game.handler.*
import pl.kerrex.duelmasters.lobby.handler.AvailableGamesHandler
import pl.kerrex.duelmasters.lobby.handler.JoinGameHandler
import pl.kerrex.duelmasters.lobby.handler.RegistrationHandler

fun main(args: Array<String>) {
    initContext()
    embeddedServer(Netty, 9999) { app() }
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
        registerLobbyHandlers()
        registerGameHandlers()
    }
}

private fun Routing.registerGameHandlers() {
    post("add-to-mana") {
        val handler: AddToManaHandler by Kodein.global.instance()
        handler.handle(call)
    }
    post("attack") {
        val handler: AttackHandler by Kodein.global.instance()
        handler.handle(call)
    }
    post("attack-shield") {
        val handler: AttackShieldHandler by Kodein.global.instance()
        handler.handle(call)
    }
    post("block") {
        val handler: BlockHandler by Kodein.global.instance()
        handler.handle(call)
    }
    get("poll-game") {
        val handler: PollGameHandler by Kodein.global.instance()
        handler.handle(call)
    }
    post("summon") {
        val handler: SummonHandler by Kodein.global.instance()
        handler.handle(call)
    }
    post("switch-turns") {
        val handler: SwitchTurnHandler by Kodein.global.instance()
        handler.handle(call)
    }
}

private fun Routing.registerLobbyHandlers() {
    post("/register") {
        val handler: RegistrationHandler by Kodein.global.instance()
        handler.handle(call)
    }
    get("/available-games") {
        val handler: AvailableGamesHandler by Kodein.global.instance()
        handler.handle(call)
    }
    post("/join-game") {
        val handler: JoinGameHandler by Kodein.global.instance()
        handler.handle(call)
    }
}