package pl.kerrex.duelmasters.lobby.handler

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import pl.kerrex.duelmasters.common.beans.Game
import pl.kerrex.duelmasters.common.handler.RequestHandler
import pl.kerrex.duelmasters.lobby.dto.AvailableGameDTO
import pl.kerrex.duelmasters.lobby.service.LobbyService

open class AvailableGamesHandler(private val lobbyService: LobbyService) : RequestHandler {
    override suspend fun handle(call: ApplicationCall) {
        val availableGames = lobbyService.getAvailableGames()
        val dtos = availableGames.map(this::toDto)

        call.respond(dtos)
    }

    private fun toDto(game: Game): AvailableGameDTO {
        return AvailableGameDTO(game.name, game.uuid)
    }
}