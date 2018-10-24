package pl.kerrex.duelmasters.game.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveText
import io.ktor.response.respond
import pl.kerrex.duelmasters.common.Kson
import pl.kerrex.duelmasters.common.beans.RequestStatus
import pl.kerrex.duelmasters.common.handler.RequestHandler
import pl.kerrex.duelmasters.game.dto.ActionDTO
import pl.kerrex.duelmasters.game.service.GameBoardService

class SwitchTurnHandler(private val gameBoardService: GameBoardService) : RequestHandler {
    override suspend fun handle(call: ApplicationCall) {
        val actionDTO: ActionDTO = Kson.fromJson(call.receiveText())
        val result = gameBoardService.switchTurns(actionDTO)

        call.respond(RequestStatus.ofBoolean(result))
    }
}