package pl.kerrex.duelmasters.common

enum class TurnStatus(val orderNumber: Int) {
    BLOCKING(-1),
    BEGIN(0),
    MANA_PUT(1),
    SUMMONINING(2),
    ATTACKING(3),

}