package com.example.ktan.data.model

data class BoardState(
    val tiles: List<HexTile>,
    val players: List<Player>,
    val currentPlayerIndex: Int,
    val diceResult: Pair<Int, Int>?,
    val phase: GamePhase,
    val roads: List<Road> = emptyList(),
    val settlements: List<Settlement> = emptyList(),
    val devCardsDeck: List<DevelopmentCard> = emptyList()
)
