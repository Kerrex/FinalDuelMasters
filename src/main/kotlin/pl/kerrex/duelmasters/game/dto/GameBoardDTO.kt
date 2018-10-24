package pl.kerrex.duelmasters.game.dto

import pl.kerrex.duelmasters.common.TurnStatus

data class GameBoardDTO(val uuid: String, val hostTurn: Boolean, val turnStatus: TurnStatus,
                        val host: PlayerDTO, val guest: PlayerDTO)