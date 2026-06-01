package com.example.ktan.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ktan.data.model.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class PendingTrade(
    val proposerId: Int,
    val receiverId: Int,
    val offered: Map<ResourceType, Int>,
    val requested: Map<ResourceType, Int>
)

class GameViewModel : ViewModel() {

    var state by mutableStateOf(newGame())
        private set

    var pendingTrade by mutableStateOf<PendingTrade?>(null)
        private set

    private var placementStep by mutableStateOf(0)
    private var placementOrder by mutableStateOf(listOf<Int>())
    private var placementType by mutableStateOf(listOf<String>())

    var isBuildingRoad by mutableStateOf(false)
        private set
    var freeRoadsAvailable by mutableStateOf(0)
        private set
    var isBuildingSettlement by mutableStateOf(false)
        private set
    var isBuildingCity by mutableStateOf(false)
        private set
    var showMonopolySelection by mutableStateOf(false)
        private set

    init {
        // Default init for classic 3 players
        setupPlacementSequence(3)
        updateBuildingModes()
    }

    private fun setupPlacementSequence(playerCount: Int) {
        val order = mutableListOf<Int>()
        val types = mutableListOf<String>()
        // 1st round: forward
        for (i in 0 until playerCount) {
            order.add(i); types.add("SETTLEMENT")
            order.add(i); types.add("ROAD")
        }
        // 2nd round: backward
        for (i in (playerCount - 1) downTo 0) {
            order.add(i); types.add("SETTLEMENT")
            order.add(i); types.add("ROAD")
        }
        placementOrder = order
        placementType = types
    }

    fun startGame(mode: String, playerCount: Int = 3) {
        state = if (mode == "demo") demoGame() else newGame(playerCount)
        placementStep = 0
        setupPlacementSequence(if (mode == "demo") 3 else playerCount)
        stopBuilding()
        updateBuildingModes()
    }

    fun proposeTrade(targetPlayer: Player, offered: Map<ResourceType, Int>, requested: Map<ResourceType, Int>) {
        pendingTrade = PendingTrade(
            proposerId = state.players[state.currentPlayerIndex].id,
            receiverId = targetPlayer.id,
            offered = offered,
            requested = requested
        )
    }

    fun acceptTrade() {
        val trade = pendingTrade ?: return
        val proposer = state.players.find { it.id == trade.proposerId } ?: return
        val receiver = state.players.find { it.id == trade.receiverId } ?: return

        performTrade(proposer, receiver, trade.offered, trade.requested)
        pendingTrade = null
    }

    fun rejectTrade() {
        pendingTrade = null
    }

    private fun performTrade(
        proposer: Player,
        receiver: Player,
        offered: Map<ResourceType, Int>,
        requested: Map<ResourceType, Int>
    ) {
        val updatedPlayers = state.players.toMutableList()
        val proposerIdx = updatedPlayers.indexOfFirst { it.id == proposer.id }
        val receiverIdx = updatedPlayers.indexOfFirst { it.id == receiver.id }
        
        if (proposerIdx == -1 || receiverIdx == -1) return
        
        val newProposerRes = proposer.resources.toMutableMap()
        val newReceiverRes = receiver.resources.toMutableMap()
        
        offered.forEach { (res, qty) ->
            newProposerRes[res] = (newProposerRes[res] ?: 0) - qty
            newReceiverRes[res] = (newReceiverRes[res] ?: 0) + qty
        }
        
        requested.forEach { (res, qty) ->
            newReceiverRes[res] = (newReceiverRes[res] ?: 0) - qty
            newProposerRes[res] = (newProposerRes[res] ?: 0) + qty
        }
        
        updatedPlayers[proposerIdx] = proposer.copy(resources = newProposerRes)
        updatedPlayers[receiverIdx] = receiver.copy(resources = newReceiverRes)
        
        state = state.copy(players = updatedPlayers)
    }

    fun rollDice() {
        if (state.phase == GamePhase.PLACEMENT) return

        val d1 = (1..6).random()
        val d2 = (1..6).random()
        val sum = d1 + d2
        
        state = state.copy(diceResult = Pair(d1, d2))
        
        if (sum == 7) {
            state = state.copy(phase = GamePhase.ROBBER)
        } else {
            produceResources(sum)
        }
    }

    private fun produceResources(sum: Int) {
        val updatedPlayers = state.players.toMutableList()
        
        state.tiles.forEach { tile ->
            if (tile.number == sum && !tile.hasRobber) {
                state.settlements.forEach { settlement ->
                    if (isSettlementOnTile(settlement, tile.row, tile.col)) {
                        val playerIdx = updatedPlayers.indexOfFirst { it.id == settlement.playerId }
                        if (playerIdx != -1) {
                            val player = updatedPlayers[playerIdx]
                            val newRes = player.resources.toMutableMap()
                            val amount = if (settlement.isCity) 2 else 1
                            newRes[tile.resource] = (newRes[tile.resource] ?: 0) + amount
                            updatedPlayers[playerIdx] = player.copy(resources = newRes)
                        }
                    }
                }
            }
        }
        state = state.copy(players = updatedPlayers)
    }

    private fun isSettlementOnTile(s: Settlement, tRow: Int, tCol: Int): Boolean {
        val rowOffsets = listOf(1f, 0.5f, 0f, 0.5f, 1f)
        val sqrt3 = sqrt(3f)

        fun getVertexCoord(r: Int, c: Int, v: Int): Pair<Float, Float> {
            val cx = rowOffsets[r] * sqrt3 + c * sqrt3
            val cy = r * 1.5f
            val angle = Math.toRadians((60.0 * v) - 30.0)
            return (cx + cos(angle).toFloat()) to (cy + sin(angle).toFloat())
        }

        val (svx, svy) = getVertexCoord(s.tileRow, s.tileCol, s.vertexIndex)

        for (v in 0..5) {
            val (tvx, tvy) = getVertexCoord(tRow, tCol, v)
            if (abs(svx - tvx) < 0.1f && abs(svy - tvy) < 0.1f) return true
        }
        return false
    }

    fun moveRobber(row: Int, col: Int) {
        val newTiles = state.tiles.map { tile ->
            tile.copy(hasRobber = (tile.row == row && tile.col == col))
        }
        
        var stolenPlayerId: Int? = null
        state.settlements.forEach { s ->
            if (isSettlementOnTile(s, row, col) && s.playerId != state.players[state.currentPlayerIndex].id) {
                stolenPlayerId = s.playerId
            }
        }

        val updatedPlayers = state.players.toMutableList()
        if (stolenPlayerId != null) {
            val victimIdx = updatedPlayers.indexOfFirst { it.id == stolenPlayerId }
            if (victimIdx != -1) {
                val victim = updatedPlayers[victimIdx]
                val thiefIdx = state.currentPlayerIndex
                val thief = updatedPlayers[thiefIdx]
                
                val availableResources = victim.resources.filter { it.value > 0 }.keys.toList()
                if (availableResources.isNotEmpty()) {
                    val resToSteal = availableResources.random()
                    
                    val newVictimRes = victim.resources.toMutableMap()
                    newVictimRes[resToSteal] = newVictimRes[resToSteal]!! - 1
                    updatedPlayers[victimIdx] = victim.copy(resources = newVictimRes)
                    
                    val newThiefRes = thief.resources.toMutableMap()
                    newThiefRes[resToSteal] = (newThiefRes[resToSteal] ?: 0) + 1
                    updatedPlayers[thiefIdx] = thief.copy(resources = newThiefRes)
                }
            }
        }

        state = state.copy(tiles = newTiles, players = updatedPlayers, phase = GamePhase.MAIN)
    }

    private fun updateBuildingModes() {
        if (state.phase == GamePhase.PLACEMENT && placementStep < placementType.size) {
            isBuildingSettlement = placementType[placementStep] == "SETTLEMENT"
            isBuildingRoad = placementType[placementStep] == "ROAD"
            isBuildingCity = false
        }
    }

    fun startBuildingRoad() { 
        if (state.phase == GamePhase.PLACEMENT) return
        isBuildingRoad = true; isBuildingSettlement = false; isBuildingCity = false 
    }
    fun startBuildingSettlement() { 
        if (state.phase == GamePhase.PLACEMENT) return
        isBuildingSettlement = true; isBuildingRoad = false; isBuildingCity = false 
    }
    fun startBuildingCity() { 
        if (state.phase == GamePhase.PLACEMENT) return
        isBuildingCity = true; isBuildingRoad = false; isBuildingSettlement = false 
    }

    fun stopBuilding() {
        if (state.phase == GamePhase.PLACEMENT) return
        isBuildingRoad = false
        isBuildingSettlement = false
        isBuildingCity = false
        freeRoadsAvailable = 0
    }

    fun buildRoad(row: Int, col: Int, edgeIndex: Int) {
        val currentPlayer = state.players[state.currentPlayerIndex]
        
        if (state.phase == GamePhase.PLACEMENT) {
            if (placementStep >= placementType.size || placementType[placementStep] != "ROAD") return
            
            val newRoads = state.roads + Road(currentPlayer.id, row, col, edgeIndex)
            state = state.copy(roads = newRoads)
            advancePlacement()
            return
        }

        if (freeRoadsAvailable > 0) {
            val newRoads = state.roads + Road(currentPlayer.id, row, col, edgeIndex)
            state = state.copy(roads = newRoads)
            freeRoadsAvailable--
            if (freeRoadsAvailable == 0) {
                isBuildingRoad = false
            }
            return
        }

        val newResources = currentPlayer.resources.toMutableMap()
        newResources[ResourceType.FOREST] = (newResources[ResourceType.FOREST] ?: 0) - 1
        newResources[ResourceType.HILL] = (newResources[ResourceType.HILL] ?: 0) - 1
        
        updateStateWithNewBuilding(
            updatedPlayer = currentPlayer.copy(resources = newResources),
            newRoads = state.roads + Road(currentPlayer.id, row, col, edgeIndex)
        )
        stopBuilding()
    }

    private fun getVertexCoord(r: Int, c: Int, v: Int): Pair<Float, Float> {
        val rowOffsets = listOf(1f, 0.5f, 0f, 0.5f, 1f)
        val sqrt3 = sqrt(3f)
        val cx = rowOffsets[r] * sqrt3 + c * sqrt3
        val cy = r * 1.5f
        val angle = Math.toRadians((60.0 * v) - 30.0)
        return (cx + cos(angle).toFloat()) to (cy + sin(angle).toFloat())
    }

    private fun isVertexSame(r1: Int, c1: Int, v1: Int, r2: Int, c2: Int, v2: Int): Boolean {
        val (x1, y1) = getVertexCoord(r1, c1, v1)
        val (x2, y2) = getVertexCoord(r2, c2, v2)
        return abs(x1 - x2) < 0.1f && abs(y1 - y2) < 0.1f
    }

    private fun isRoadConnectedToVertex(road: Road, r: Int, c: Int, v: Int): Boolean {
        return isVertexSame(road.tileRow, road.tileCol, road.edgeIndex, r, c, v) ||
               isVertexSame(road.tileRow, road.tileCol, (road.edgeIndex + 1) % 6, r, c, v)
    }

    private fun hasRoadConnection(playerId: Int, r: Int, c: Int, v: Int): Boolean {
        return state.roads.any { it.playerId == playerId && isRoadConnectedToVertex(it, r, c, v) }
    }

    private fun isSettlementAtVertex(s: Settlement, r: Int, c: Int, v: Int): Boolean {
        return isVertexSame(s.tileRow, s.tileCol, s.vertexIndex, r, c, v)
    }

    fun buildSettlement(row: Int, col: Int, vertexIndex: Int) {
        val currentPlayer = state.players[state.currentPlayerIndex]

        // 1. Check if a building already exists here
        if (state.settlements.any { isSettlementAtVertex(it, row, col, vertexIndex) }) return

        // 2. Check Distance Rule (no buildings on adjacent vertices)
        val neighbors = listOf((vertexIndex + 5) % 6, (vertexIndex + 1) % 6)
        val hasNeighborBuilding = state.settlements.any { s ->
            neighbors.any { nv -> isVertexSame(s.tileRow, s.tileCol, s.vertexIndex, row, col, nv) }
        }
        if (hasNeighborBuilding) return

        if (state.phase == GamePhase.PLACEMENT) {
            if (placementStep >= placementType.size || placementType[placementStep] != "SETTLEMENT") return
            
            val newSettlement = Settlement(currentPlayer.id, row, col, vertexIndex)
            val newSettlements = state.settlements + newSettlement
            
            if (placementStep >= (placementOrder.size / 2)) {
                grantInitialResources(currentPlayer, row, col, vertexIndex)
            }
            
            val updatedPlayers = state.players.toMutableList()
            updatedPlayers[state.currentPlayerIndex] = state.players[state.currentPlayerIndex].copy(
                victoryPoints = state.players[state.currentPlayerIndex].victoryPoints + 1
            )
            
            state = state.copy(settlements = newSettlements, players = updatedPlayers)
            advancePlacement()
            return
        }

        // 3. MAIN Phase: Check road connection requirement
        if (!hasRoadConnection(currentPlayer.id, row, col, vertexIndex)) return

        val newResources = currentPlayer.resources.toMutableMap()
        newResources[ResourceType.FOREST] = (newResources[ResourceType.FOREST] ?: 0) - 1
        newResources[ResourceType.PASTURE] = (newResources[ResourceType.PASTURE] ?: 0) - 1
        newResources[ResourceType.FIELD] = (newResources[ResourceType.FIELD] ?: 0) - 1
        newResources[ResourceType.HILL] = (newResources[ResourceType.HILL] ?: 0) - 1

        updateStateWithNewBuilding(
            updatedPlayer = currentPlayer.copy(
                resources = newResources,
                victoryPoints = currentPlayer.victoryPoints + 1
            ),
            newSettlements = state.settlements + Settlement(currentPlayer.id, row, col, vertexIndex)
        )
        stopBuilding()
    }

    private fun grantInitialResources(player: Player, sRow: Int, sCol: Int, sVertex: Int) {
        val updatedPlayers = state.players.toMutableList()
        val pIdx = updatedPlayers.indexOfFirst { it.id == player.id }
        if (pIdx == -1) return
        
        val newRes = updatedPlayers[pIdx].resources.toMutableMap()
        
        state.tiles.forEach { tile ->
            if (isSettlementOnTile(Settlement(player.id, sRow, sCol, sVertex), tile.row, tile.col)) {
                if (tile.resource != ResourceType.DESERT) {
                    newRes[tile.resource] = (newRes[tile.resource] ?: 0) + 1
                }
            }
        }
        
        updatedPlayers[pIdx] = updatedPlayers[pIdx].copy(resources = newRes)
        state = state.copy(players = updatedPlayers)
    }

    private fun advancePlacement() {
        placementStep++
        if (placementStep >= placementOrder.size) {
            state = state.copy(phase = GamePhase.MAIN, currentPlayerIndex = 0)
            stopBuilding()
        } else {
            state = state.copy(currentPlayerIndex = placementOrder[placementStep])
            updateBuildingModes()
        }
    }


    fun buildCity(row: Int, col: Int, vertexIndex: Int) {
        if (state.phase == GamePhase.PLACEMENT) return
        
        val currentPlayer = state.players[state.currentPlayerIndex]
        
        // Find existing settlement physically at this vertex
        val settlementIndex = state.settlements.indexOfFirst { 
            isSettlementAtVertex(it, row, col, vertexIndex) 
        }

        // Must be a settlement here, owned by player, and not already a city
        if (settlementIndex == -1) return
        val target = state.settlements[settlementIndex]
        if (target.playerId != currentPlayer.id || target.isCity) return

        val newResources = currentPlayer.resources.toMutableMap()
        newResources[ResourceType.FIELD] = (newResources[ResourceType.FIELD] ?: 0) - 2
        newResources[ResourceType.MOUNTAIN] = (newResources[ResourceType.MOUNTAIN] ?: 0) - 3

        val newSettlements = state.settlements.toMutableList()
        newSettlements[settlementIndex] = target.copy(isCity = true)

        updateStateWithNewBuilding(
            updatedPlayer = currentPlayer.copy(
                resources = newResources,
                victoryPoints = currentPlayer.victoryPoints + 1
            ),
            newSettlements = newSettlements
        )
        stopBuilding()
    }

    private fun updateStateWithNewBuilding(
        updatedPlayer: Player,
        newRoads: List<Road>? = null,
        newSettlements: List<Settlement>? = null
    ) {
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.currentPlayerIndex] = updatedPlayer
        
        state = state.copy(
            players = updatedPlayers,
            roads = newRoads ?: state.roads,
            settlements = newSettlements ?: state.settlements
        )
    }


    fun buyDevelopmentCard() {
        val currentPlayer = state.players[state.currentPlayerIndex]
        val deck = state.devCardsDeck.toMutableList()
        if (deck.isEmpty()) return

        val newResources = currentPlayer.resources.toMutableMap()
        newResources[ResourceType.PASTURE] = (newResources[ResourceType.PASTURE] ?: 0) - 1
        newResources[ResourceType.FIELD] = (newResources[ResourceType.FIELD] ?: 0) - 1
        newResources[ResourceType.MOUNTAIN] = (newResources[ResourceType.MOUNTAIN] ?: 0) - 1

        val card = deck.removeAt(0)
        val newDevCards = currentPlayer.developmentCards + card
        val vpBonus = if (card == DevelopmentCard.VICTORY_POINT) 1 else 0

        val updatedPlayer = currentPlayer.copy(
            resources = newResources,
            developmentCards = newDevCards,
            victoryPoints = currentPlayer.victoryPoints + vpBonus
        )
        
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.currentPlayerIndex] = updatedPlayer

        state = state.copy(
            players = updatedPlayers,
            devCardsDeck = deck
        )
    }

    fun playDevelopmentCard(card: DevelopmentCard) {
        val currentPlayer = state.players[state.currentPlayerIndex]
        if (!currentPlayer.developmentCards.contains(card)) return
        if (card == DevelopmentCard.VICTORY_POINT) return

        val newDevCards = currentPlayer.developmentCards.toMutableList()
        newDevCards.remove(card)

        var updatedPlayer = currentPlayer.copy(developmentCards = newDevCards)
        var nextPhase = state.phase

        when (card) {
            DevelopmentCard.KNIGHT -> {
                updatedPlayer = updatedPlayer.copy(knightsPlayed = updatedPlayer.knightsPlayed + 1)
                nextPhase = GamePhase.ROBBER
            }
            DevelopmentCard.YEAR_OF_PLENTY -> {
                val resTypes = ResourceType.entries.filter { it != ResourceType.DESERT }
                val r1 = resTypes.random()
                val r2 = resTypes.random()
                val newRes = updatedPlayer.resources.toMutableMap()
                newRes[r1] = (newRes[r1] ?: 0) + 1
                newRes[r2] = (newRes[r2] ?: 0) + 1
                updatedPlayer = updatedPlayer.copy(resources = newRes)
            }
            DevelopmentCard.ROAD_BUILDING -> {
                freeRoadsAvailable = 2
                isBuildingRoad = true
            }
            DevelopmentCard.MONOPOLY -> {
                showMonopolySelection = true
            }
            else -> {}
        }

        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.currentPlayerIndex] = updatedPlayer
        state = state.copy(players = updatedPlayers, phase = nextPhase)
        
        if (card == DevelopmentCard.KNIGHT) {
            checkLargestArmy()
        }
    }

    fun applyMonopoly(resource: ResourceType) {
        val currentPlayer = state.players[state.currentPlayerIndex]
        var totalStolen = 0
        val playersList = state.players.map { p ->
            if (p.id != currentPlayer.id) {
                val amount = p.resources[resource] ?: 0
                totalStolen += amount
                p.copy(resources = p.resources + (resource to 0))
            } else p
        }
        
        val updatedPlayers = playersList.toMutableList()
        val myIdx = updatedPlayers.indexOfFirst { it.id == currentPlayer.id }
        val myNewRes = currentPlayer.resources.toMutableMap()
        myNewRes[resource] = (myNewRes[resource] ?: 0) + totalStolen
        updatedPlayers[myIdx] = currentPlayer.copy(resources = myNewRes)
        
        state = state.copy(players = updatedPlayers)
        showMonopolySelection = false
    }

    fun cancelMonopoly() {
        showMonopolySelection = false
    }

    private fun checkLargestArmy() {
        val players = state.players.toMutableList()
        val currentLeader = players.find { it.hasLargestArmy }
        val leaderKnights = currentLeader?.knightsPlayed ?: 2

        val newLeader = players.filter { it.knightsPlayed > leaderKnights }
            .maxByOrNull { it.knightsPlayed }

        if (newLeader != null && newLeader.id != currentLeader?.id) {
            val updatedPlayers = players.map { p ->
                when (p.id) {
                    currentLeader?.id -> p.copy(hasLargestArmy = false, victoryPoints = p.victoryPoints - 2)
                    newLeader.id -> p.copy(hasLargestArmy = true, victoryPoints = p.victoryPoints + 2)
                    else -> p
                }
            }
            state = state.copy(players = updatedPlayers)
        }
    }

    fun nextTurn() {
        if (state.phase == GamePhase.PLACEMENT) return

        val next = (state.currentPlayerIndex + 1) % state.players.size
        state = state.copy(
            currentPlayerIndex = next,
            diceResult = null,
            phase = GamePhase.MAIN
        )
    }
}
