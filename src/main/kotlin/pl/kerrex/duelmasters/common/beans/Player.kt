package pl.kerrex.duelmasters.common.beans

import pl.kerrex.duelmasters.common.Card
import java.io.Serializable
import java.util.*

data class Player(val uuid: String,
                  val deck: MutableList<Card>,
                  val hand: MutableList<Card>,
                  val battleZone: MutableList<Card>,
                  val manaZone: MutableList<Card>,
                  val shields: MutableList<Card?>,
                  val graveyard: MutableList<Card>,
                  var manaPoints: Int) : Serializable {
    companion object {
        fun newPlayer(deck: List<Card>): Player {
            val uuid = UUID.randomUUID().toString()
            val shuffledDeck = deck.shuffled().toMutableList()
            val shields = shuffledDeck.asSequence()
                                      .take(INITIAL_SHIELD_SIZE)
                                      .toMutableList()

            shuffledDeck.drop(INITIAL_SHIELD_SIZE)

            return Player(uuid, shuffledDeck, mutableListOf(), mutableListOf(), mutableListOf(), shields.toMutableList(),
                          mutableListOf(), 0)
        }

        private const val INITIAL_SHIELD_SIZE = 5
    }
}