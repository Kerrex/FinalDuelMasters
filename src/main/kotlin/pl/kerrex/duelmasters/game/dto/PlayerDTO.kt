package pl.kerrex.duelmasters.game.dto

import pl.kerrex.duelmasters.common.Card


data class PlayerDTO(val uuid: String, val deckCount: Int, val handCount: Int,
                     val battleZone: List<Card>, val manaZone: List<Card>,
                     val shieldsStatus: List<Boolean>,
                     val graveyard: List<Card>,
                     val manaPoints: Int)