package pl.kerrex.duelmasters.common

import pl.kerrex.duelmasters.common.beans.Civilization
import pl.kerrex.duelmasters.common.beans.Game
import pl.kerrex.duelmasters.common.beans.Player
import java.io.Serializable

interface Card: Serializable {
    val uuid: String
    var attackPoints: Int
    val civilization: Civilization
    var sickness: Boolean
    var tapped: Boolean
    var isBlocker: Boolean
    var isSpell: Boolean
    val manaPoints: Int

    fun onAttack(game: Game) {

    }
    fun onManaZone(game: Game) {

    }
    fun onBattleZone(game: Game) {

    }
    fun onDying(game: Game, owner: Player) {

    }
    fun onShieldBreak(game: Game) {

    }
}