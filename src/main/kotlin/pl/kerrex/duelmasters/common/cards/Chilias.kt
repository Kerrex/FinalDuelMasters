package pl.kerrex.duelmasters.common.cards

import pl.kerrex.duelmasters.common.Card
import pl.kerrex.duelmasters.common.beans.Civilization
import pl.kerrex.duelmasters.common.beans.Game
import pl.kerrex.duelmasters.common.beans.Player

class Chilias: Card {
    override val uuid: String = "chiliastheoracle"
    override var attackPoints: Int = 2500
    override val civilization: Civilization = Civilization.LIGHT
    override var sickness: Boolean = true
    override var tapped: Boolean = false
    override var isBlocker: Boolean = false
    override var isSpell: Boolean = false
    override val manaPoints: Int = 4

    override fun onDying(game: Game, owner: Player) {
        owner.graveyard.remove(this)
        owner.hand.add(this)
    }

}