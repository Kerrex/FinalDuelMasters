package pl.kerrex.duelmasters.lobby.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveText
import io.ktor.response.respond
import pl.kerrex.duelmasters.common.Kson
import pl.kerrex.duelmasters.common.beans.RequestStatus
import pl.kerrex.duelmasters.lobby.dto.DeckInputDTO
import pl.kerrex.duelmasters.common.handler.RequestHandler
import pl.kerrex.duelmasters.lobby.service.CardProvider
import pl.kerrex.duelmasters.lobby.service.LobbyService

open class JoinGameHandler(private val cardProvider: CardProvider,
                           private val lobbyService: LobbyService) : RequestHandler {
    override suspend fun handle(call: ApplicationCall) {
        val json = call.receiveText()
        val guestDeck: DeckInputDTO = Kson.fromJson(json)
        val guestCards = guestDeck.cardNames.map(cardProvider::getCardByName)

        val status = lobbyService.joinGame(guestDeck.gameUuid!!, guestCards)

        call.respond(RequestStatus.ofBoolean(status))
    }
}