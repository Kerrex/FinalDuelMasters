package pl.kerrex.duelmasters.game.handler

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveText
import io.ktor.response.respond
import pl.kerrex.duelmasters.common.Kson
import pl.kerrex.duelmasters.common.handler.RequestHandler
import pl.kerrex.duelmasters.game.dto.AttackShieldDTO
import pl.kerrex.duelmasters.game.service.CardActionsService
import pl.kerrex.duelmasters.game.service.GameBoardService

open class AttackShieldHandler(private val actionsService: CardActionsService,
                               private val gameBoardService: GameBoardService) : RequestHandler {
    override suspend fun handle(call: ApplicationCall) {
        val action: AttackShieldDTO = Kson.fromJson(call.receiveText())
        actionsService.attackShield(action)

        val gameStatus = gameBoardService.getGameBoardDto(action.gameUuid)
        call.respond(gameStatus)
    }

}