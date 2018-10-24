package pl.kerrex.duelmasters.common.repository

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import pl.kerrex.duelmasters.common.beans.Game
import pl.kerrex.duelmasters.common.beans.Player
import pl.riscosoftware.cache.HazelcastCache

open class GameRepository(private val hazelcastInstance: HazelcastInstance) {
    fun getAllGames(): List<Game> {
        return getGamesMap().values
                            .toList()
    }

    fun getAvailableGames(): List<Game> {
        return getGamesMap().values
                            .filter { it.guest == null }
                            .toList()
    }

    fun getGame(uuid: String): Game? {
        return getGamesMap()[uuid]
    }

    fun saveGame(game: Game) {
        val games = getGamesMap()
        games[game.uuid] = game
    }

    private fun getGamesMap(): IMap<String, Game> {
        return hazelcastInstance.getMap<String, Game>(HazelcastCache.GAMES)
    }
}