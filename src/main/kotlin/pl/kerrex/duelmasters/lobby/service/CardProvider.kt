package pl.kerrex.duelmasters.lobby.service

import pl.kerrex.duelmasters.common.Card
import kotlin.reflect.full.createInstance

open class CardProvider {
    fun getCardByName(name: String): Card {
        val clazz = Class.forName(CARD_PACKAGE_PREFIX + name).kotlin
        return clazz.createInstance() as Card
    }

    companion object {
        private const val CARD_PACKAGE_PREFIX = "pl.kerrex.duelmasters.common.cards."
    }
}