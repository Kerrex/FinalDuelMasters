package pl.kerrex.duelmasters.lobby.service

import com.hazelcast.core.HazelcastInstance
import pl.kerrex.duelmasters.common.beans.Game
import pl.kerrex.duelmasters.common.beans.Player
import pl.kerrex.duelmasters.common.Card
import pl.kerrex.duelmasters.common.repository.GameRepository
import pl.riscosoftware.cache.HazelcastCache
import java.util.*

open class LobbyService(private val gameRepository: GameRepository) {
    fun registerNewGame(name: String, hostDeck: List<Card>) : Boolean {
        val newGameUuid = UUID.randomUUID().toString()
        val newGame = Game(name, newGameUuid, Player.newPlayer(hostDeck), null)
        gameRepository.saveGame(newGame)

        return true
    }

    fun joinGame(uuid: String, guestDeck: List<Card>): Boolean {
        val existingGame = gameRepository.getGame(uuid)
        val guest = Player.newPlayer(guestDeck)
        existingGame?.guest = guest
        existingGame?.let { gameRepository.saveGame(it) }

        return existingGame != null
    }

    fun getAvailableGames(): List<Game> {
        return gameRepository.getAvailableGames()
    }

    companion object {
        private const val GAMES = HazelcastCache.GAMES
    }
}