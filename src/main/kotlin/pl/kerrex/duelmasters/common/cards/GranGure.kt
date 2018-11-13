package pl.kerrex.duelmasters.common.cards

import pl.kerrex.duelmasters.common.Card
import pl.kerrex.duelmasters.common.beans.Civilization


class GranGure : Card {
    override val uuid: String = "grangurespaceguardian"
    override var attackPoints: Int = 9000
    override val civilization: Civilization = Civilization.LIGHT
    override var sickness: Boolean = true
    override var tapped: Boolean = false
    override var isBlocker: Boolean = true
    override var isSpell: Boolean = false
    override val manaPoints: Int = 6
}