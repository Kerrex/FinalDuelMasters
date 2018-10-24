package pl.kerrex.duelmasters.game.dto

data class AttackShieldDTO(val gameUuid: String, val playerUuid: String,
                           val cardUuid: String, val shields: List<String>)