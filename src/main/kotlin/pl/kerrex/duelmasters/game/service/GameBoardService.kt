package pl.kerrex.duelmasters.game.service

import com.hazelcast.core.HazelcastInstance
import pl.kerrex.duelmasters.common.Card
import pl.kerrex.duelmasters.common.TurnStatus
import pl.kerrex.duelmasters.common.beans.Game
import pl.kerrex.duelmasters.common.beans.Player
import pl.kerrex.duelmasters.common.repository.GameRepository
import pl.kerrex.duelmasters.game.dto.ActionDTO
import pl.kerrex.duelmasters.game.dto.GameBoardDTO
import pl.kerrex.duelmasters.game.dto.PlayerDTO
import pl.riscosoftware.cache.HazelcastCache

open class GameBoardService(private val gameRepository: GameRepository) {
    fun getGameBoardDto(uuid: String): GameBoardDTO {
        val game = gameRepository.getGame(uuid)
        val hostDto = toPlayerDto(game!!.host)
        val guestDto = toPlayerDto(game.guest!!)

        return GameBoardDTO(game.uuid, game.hostTurn, game.turnStatus, hostDto, guestDto)
    }

    fun switchTurns(action: ActionDTO): Boolean {
        val game = gameRepository.getGame(action.gameUuid)!!
        val currentPlayer = getCurrentPlayer(game)
        if (currentPlayer.uuid != action.playerUuid) {
            return false
        }

        switchTurns(game)
        return true
    }

    private fun switchTurns(game: Game) {
        game.hostTurn = !game.hostTurn
        game.turnStatus = TurnStatus.BEGIN
        val newCurrentPlayer = getCurrentPlayer(game)
        newCurrentPlayer.manaPoints = newCurrentPlayer.manaZone.size
        untapAllBattlezoneCards(newCurrentPlayer)
        removeSummoningSickness(newCurrentPlayer)

        takeCardToHand(newCurrentPlayer)
        gameRepository.saveGame(game)
    }

    private fun removeSummoningSickness(newCurrentPlayer: Player) {
        newCurrentPlayer.battleZone.forEach { it.sickness = false }
    }

    private fun untapAllBattlezoneCards(newCurrentPlayer: Player) {
        newCurrentPlayer.battleZone.forEach { it.tapped = false }
    }

    private fun takeCardToHand(player: Player) {
        val cardToTake = player.deck.firstOrNull()
        player.deck.drop(1)
        cardToTake?.let { player.hand.add(it) }
    }

    private fun getCurrentPlayer(game: Game): Player {
        return if (game.hostTurn) game.host else game.guest!!
    }

    private fun toPlayerDto(player: Player): PlayerDTO {
        return PlayerDTO(player.uuid, player.deck.size, player.hand.size,
                         player.battleZone.toList(), player.manaZone.toList(),
                         getShieldsStatus(player.shields), player.graveyard.toList(), player.manaPoints)
    }

    private fun getShieldsStatus(shields: List<Card?>): List<Boolean> {
        return shields.map { it != null }
    }
}