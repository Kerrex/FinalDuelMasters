package pl.kerrex.duelmasters.common

import java.util.*

abstract class DefaultCard : Card {
    override val uuid: String = UUID.randomUUID().toString()
    override var tapped: Boolean = true
    override var sickness: Boolean = true
}