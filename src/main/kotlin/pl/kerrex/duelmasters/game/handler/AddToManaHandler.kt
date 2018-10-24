package pl.kerrex.duelmasters.game.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveText
import io.ktor.response.respond
import pl.kerrex.duelmasters.common.Kson
import pl.kerrex.duelmasters.common.handler.RequestHandler
import pl.kerrex.duelmasters.game.dto.ActionDTO
import pl.kerrex.duelmasters.game.service.CardActionsService
import pl.kerrex.duelmasters.game.service.GameBoardService

open class AddToManaHandler(private val cardActionsService: CardActionsService,
                            private val gameBoardService: GameBoardService) : RequestHandler {
    override suspend fun handle(call: ApplicationCall) {
        val actionDto: ActionDTO = Kson.fromJson(call.receiveText())
        cardActionsService.addToMana(actionDto)

        val newBoardState = gameBoardService.getGameBoardDto(actionDto.gameUuid)
        call.respond(newBoardState)
    }
}