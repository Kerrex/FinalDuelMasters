package pl.kerrex.duelmasters.game.service

import pl.kerrex.duelmasters.common.Card
import pl.kerrex.duelmasters.common.TurnStatus
import pl.kerrex.duelmasters.common.beans.Game
import pl.kerrex.duelmasters.common.beans.Player
import pl.kerrex.duelmasters.common.repository.GameRepository
import pl.kerrex.duelmasters.game.dto.ActionDTO
import pl.kerrex.duelmasters.game.dto.AttackShieldDTO
import kotlin.math.min

class CardActionsService(private val gameRepository: GameRepository) {
    fun addToMana(actionDTO: ActionDTO): Boolean {
        val game = gameRepository.getGame(actionDTO.gameUuid)
        if (!isActionValid(game, actionDTO, this::cannotPutMana)) {
            return false
        }

        game?.turnStatus = TurnStatus.MANA_PUT
        val currentPlayer = getCurrentPlayer(game!!)
        val cardToMove = currentPlayer?.hand?.find { it.uuid == actionDTO.cardUuid }
        currentPlayer?.hand?.remove(cardToMove)
        cardToMove?.let { currentPlayer.manaZone.add(it) }
        currentPlayer?.manaPoints.let { it?.plus(1) }

        cardToMove?.onManaZone(game)
        gameRepository.saveGame(game)

        return true
    }

    private fun isActionValid(game: Game?, actionDTO: ActionDTO, verificationFunc: (Game) -> Boolean): Boolean {
        if (game == null || verificationFunc(game) || isCardNotOwnedByPlayer(game, actionDTO)) {
            return false
        }
        return true
    }

    private fun isCardNotOwnedByPlayer(game: Game, actionDTO: ActionDTO): Boolean {
        val currentPlayer = getCurrentPlayer(game)
        if (currentPlayer?.uuid != actionDTO.playerUuid) {
            return true
        }
        return false
    }

    private fun cannotPutMana(game: Game?) =
            game == null || game.turnStatus.orderNumber > TurnStatus.MANA_PUT.orderNumber

    private fun getCurrentPlayer(game: Game): Player? = if (game.hostTurn) game.host else game.guest


    fun summon(actionDto: ActionDTO): Boolean {
        val game = gameRepository.getGame(actionDto.gameUuid)
        if (!isActionValid(game, actionDto, this::cannotSummonCreature)) {
            return false
        }

        game?.turnStatus = TurnStatus.SUMMONINING
        val currentPlayer = getCurrentPlayer(game!!)
        val cardToSummon = currentPlayer?.hand?.find { it.uuid == actionDto.cardUuid }
        currentPlayer?.hand?.remove(cardToSummon)
        cardToSummon?.let { currentPlayer.battleZone.add(it) }
        currentPlayer?.manaPoints?.let { playerMana ->
            cardToSummon?.manaPoints?.let { playerMana.minus(it) } }

        cardToSummon?.onBattleZone(game)
        gameRepository.saveGame(game)

        return true

    }

    private fun cannotSummonCreature(game: Game?) =
            game == null || game.turnStatus.orderNumber > TurnStatus.SUMMONINING.orderNumber

    fun attack(action: ActionDTO): Boolean {
        val game = gameRepository.getGame(action.gameUuid)
        if (!isActionValid(game, action, this::cannotAttack)) {
            return false
        }

        val opponent = getOpponent(game!!)
        val attackedCard = opponent?.battleZone?.find { it.uuid == action.targetCardUuid }
        if (!attackedCard?.tapped!! || attackedCard.sickness) {
            return false
        }

        game.turnStatus = TurnStatus.ATTACKING
        val currentPlayer = getCurrentPlayer(game)
        val attackingCard = currentPlayer?.battleZone?.find { it.uuid == action.cardUuid }
        attackingCard?.tapped = true
        attackingCard?.onAttack(game)

        handleAttackEvent(attackedCard, attackingCard, opponent, currentPlayer!!, game)

        gameRepository.saveGame(game)

        return true

    }

    private fun handleAttackEvent(opponentCard: Card, currentPlayerCard: Card?, opponent: Player, currentPlayer: Player, game: Game) {
        when {
            opponentCard.attackPoints < currentPlayerCard?.attackPoints!! -> moveFromBattleZoneToGraveyard(opponent, opponentCard, game)
            opponentCard.attackPoints > currentPlayerCard.attackPoints -> moveFromBattleZoneToGraveyard(currentPlayer, currentPlayerCard, game)
            else -> {
                moveFromBattleZoneToGraveyard(opponent, opponentCard, game)
                moveFromBattleZoneToGraveyard(currentPlayer, currentPlayerCard, game)
            }
        }
    }

    private fun moveFromBattleZoneToGraveyard(player: Player, card: Card, game: Game) {
        player.battleZone.remove(card)
        player.graveyard.add(card)
        card.onDying(game, player)
    }

    private fun getOpponent(game: Game): Player? = if (game.hostTurn) game.guest else game.host

    private fun cannotAttack(game: Game?) =
            game == null || game.turnStatus.orderNumber > TurnStatus.ATTACKING.orderNumber

    fun attackShield(action: AttackShieldDTO): Boolean {
        val game = gameRepository.getGame(action.gameUuid)
        if (game == null || cannotAttack(game) || isCardNotOwnedByPlayer(game, action)) {
            return false
        }

        val opponent = getOpponent(game)
        val opponentHasBlockers = opponent?.battleZone?.any { it.isBlocker } ?: false
        if (opponentHasBlockers) {
            game.hostTurn = !game.hostTurn
            game.turnStatus = TurnStatus.BLOCKING
            return true
        }

        val player = getCurrentPlayer(game)
        val attackingCard = player?.battleZone?.find { it.uuid == action.cardUuid }

        breakShields(attackingCard, action, opponent, game)

        gameRepository.saveGame(game)
        return true
    }

    private fun breakShields(attackingCard: Card?, action: AttackShieldDTO, opponent: Player?, game: Game) {
        val breakCount = calculateBreakCount(attackingCard, action)
        for (i in 0 until breakCount) {
            val cardUuidToBreak = action.shields[i]
            val cardToBreak = opponent?.shields?.find { it?.uuid == cardUuidToBreak }
            val index = opponent?.shields?.indexOf(cardToBreak)
            index?.let { opponent.shields.set(it, null) }

            cardToBreak?.let { opponent.graveyard.add(it) }
            cardToBreak?.onShieldBreak(game)
        }
    }

    private fun calculateBreakCount(attackingCard: Card?, action: AttackShieldDTO) =
            min((attackingCard?.attackPoints ?: 0 / 5000), action.shields.size)

    private fun isCardNotOwnedByPlayer(game: Game, actionDTO: AttackShieldDTO): Boolean {
        val actionDto = ActionDTO(actionDTO.gameUuid, actionDTO.playerUuid, actionDTO.cardUuid, null)
        return isCardNotOwnedByPlayer(game, actionDto)

    }

    fun block(actionDto: ActionDTO): Boolean {
        val game = gameRepository.getGame(actionDto.gameUuid)
        if (isActionValid(game, actionDto, this::cannotBlock)) {
            return false
        }

        val blockingPlayer = getCurrentPlayer(game!!)
        val blocker = blockingPlayer?.battleZone?.find { it.uuid == actionDto.cardUuid }
        if (blocker?.tapped == true) {
            return false
        }

        val blockedPlayer = getOpponent(game)
        val blocked = blockedPlayer?.battleZone?.find { it.uuid == actionDto.targetCardUuid }
        blocked?.tapped = true
        blocker?.tapped = true

        handleAttackEvent(blocked!!, blocker, blockedPlayer, blockingPlayer!!, game)
        game.hostTurn = !game.hostTurn
        game.turnStatus = TurnStatus.ATTACKING

        gameRepository.saveGame(game)

        return true

    }

    private fun cannotBlock(game: Game): Boolean {
        return game.turnStatus != TurnStatus.BLOCKING
    }

}
