package pl.kerrex.duelmasters.common.beans

import pl.kerrex.duelmasters.common.TurnStatus

data class Game(val name: String, val uuid: String,
                val host: Player, var guest: Player?, var hostTurn: Boolean = false,
                var turnStatus: TurnStatus = TurnStatus.BEGIN) {
}