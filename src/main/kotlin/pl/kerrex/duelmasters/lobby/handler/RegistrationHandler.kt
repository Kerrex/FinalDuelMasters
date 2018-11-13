package pl.kerrex.duelmasters.lobby.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveText
import io.ktor.response.respond
import mu.KotlinLogging
import pl.kerrex.duelmasters.common.Kson
import pl.kerrex.duelmasters.common.beans.RequestStatus
import pl.kerrex.duelmasters.lobby.dto.DeckInputDTO
import pl.kerrex.duelmasters.common.handler.RequestHandler
import pl.kerrex.duelmasters.lobby.service.CardProvider
import pl.kerrex.duelmasters.lobby.service.LobbyService

class RegistrationHandler(private val cardProvider: CardProvider,
                          private val lobbyService: LobbyService) : RequestHandler {
    override suspend fun handle(call: ApplicationCall) {
        val json = call.receiveText()
        val hostDeck: DeckInputDTO = Kson.fromJson(json)
//        if (hostDeck.cardNames.size < 40) {
//            call.respond(RequestStatus.ofBoolean(false))
//            return
//        }
        val deck = hostDeck.cardNames.map(cardProvider::getCardByName)

        val status = lobbyService.registerNewGame(hostDeck.gameName!!, deck)
        logger.info { "Successfuly registered game ${hostDeck.gameName}" }
        call.respond(RequestStatus.ofBoolean(status))
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}