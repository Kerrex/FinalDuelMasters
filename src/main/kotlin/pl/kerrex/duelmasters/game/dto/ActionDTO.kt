package pl.kerrex.duelmasters.game.dto

data class ActionDTO(val gameUuid: String, val playerUuid: String,
                     val cardUuid: String?, val targetCardUuid: String?)