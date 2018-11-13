package pl.kerrex.duelmasters.common.cards

import pl.kerrex.duelmasters.common.Card
import pl.kerrex.duelmasters.common.beans.Civilization

class EmeraldGrass : Card {
    override var sickness: Boolean = true
    override val uuid: String = "emeraldgrass"
    override var attackPoints: Int = 3000
    override val civilization: Civilization = Civilization.LIGHT
    override var tapped: Boolean = false
    override var isBlocker: Boolean = true
    override var isSpell: Boolean = false
    override val manaPoints: Int = 2
}