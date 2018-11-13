package pl.kerrex.duelmasters.common.beans

import pl.kerrex.duelmasters.common.TurnStatus
import java.io.Serializable

data class Game(val name: String, val uuid: String,
                val host: Player, var guest: Player?, var hostTurn: Boolean = false,
                var turnStatus: TurnStatus = TurnStatus.BEGIN) : Serializable {

    fun getCurrentPlayer(): Player? = if (hostTurn) host else guest;
}