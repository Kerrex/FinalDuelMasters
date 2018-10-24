package pl.kerrex.duelmasters.game.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveText
import io.ktor.response.respond
import pl.kerrex.duelmasters.common.Kson
import pl.kerrex.duelmasters.common.handler.RequestHandler
import pl.kerrex.duelmasters.game.dto.PollInputDTO
import pl.kerrex.duelmasters.game.service.GameBoardService

class PollGameHandler(private val gameBoardService: GameBoardService) : RequestHandler {
    override suspend fun handle(call: ApplicationCall) {
        val pollInput: PollInputDTO = Kson.fromJson(call.receiveText())
        val gameBoard = gameBoardService.getGameBoardDto(pollInput.uuid)

        call.respond(gameBoard)
    }

}