package com.example.ktan.data.model

import androidx.compose.ui.graphics.Color

fun sampleBoard(): BoardState = newGame(3)

fun newGame(playerCount: Int = 3): BoardState {
    val layout = listOf(
        listOf(0, 1, 2),
        listOf(0, 1, 2, 3),
        listOf(0, 1, 2, 3, 4),
        listOf(0, 1, 2, 3),
        listOf(0, 1, 2)
    )
    val resources = listOf(
        ResourceType.FOREST, ResourceType.FOREST, ResourceType.FOREST, ResourceType.FOREST,
        ResourceType.PASTURE, ResourceType.PASTURE, ResourceType.PASTURE, ResourceType.PASTURE,
        ResourceType.FIELD, ResourceType.FIELD, ResourceType.FIELD, ResourceType.FIELD,
        ResourceType.HILL, ResourceType.HILL, ResourceType.HILL,
        ResourceType.MOUNTAIN, ResourceType.MOUNTAIN, ResourceType.MOUNTAIN,
        ResourceType.DESERT
    ).shuffled()
    val numbers = listOf(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12).shuffled()
    
    var tileIndex = 0
    var numIndex = 0
    val tiles = mutableListOf<HexTile>()
    layout.forEachIndexed { row, cols ->
        cols.forEach { col ->
            val res = resources[tileIndex++]
            val num = if (res == ResourceType.DESERT) null else numbers[numIndex++]
            tiles.add(HexTile(col, row, res, num, hasRobber = res == ResourceType.DESERT))
        }
    }

    val devCardsDeck = (
        List(14) { DevelopmentCard.KNIGHT } +
        List(5) { DevelopmentCard.VICTORY_POINT } +
        List(2) { DevelopmentCard.ROAD_BUILDING } +
        List(2) { DevelopmentCard.YEAR_OF_PLENTY } +
        List(2) { DevelopmentCard.MONOPOLY }
    ).shuffled()

    val basePlayers = listOf(
        Player(1, "Joueur 1", Color(0xFFE63946), 0, emptyMap()),
        Player(2, "Joueur 2", Color(0xFF457B9D), 0, emptyMap()),
        Player(3, "Joueur 3", Color(0xFF2A9D8F), 0, emptyMap()),
        Player(4, "Joueur 4", Color(0xFFF39C12), 0, emptyMap())
    )

    return BoardState(
        tiles = tiles,
        players = basePlayers.take(playerCount),
        currentPlayerIndex = 0,
        diceResult = null,
        phase = GamePhase.PLACEMENT,
        roads = emptyList(),
        settlements = emptyList(),
        devCardsDeck = devCardsDeck
    )
}

fun demoGame(): BoardState {
    val game = newGame(3)
    return game.copy(
        phase = GamePhase.MAIN,
        currentPlayerIndex = 0,
        players = listOf(
            Player(1, "Joueur 1", Color(0xFFE63946), 4, mapOf(
                ResourceType.FOREST to 5, ResourceType.HILL to 5,
                ResourceType.FIELD to 2, ResourceType.PASTURE to 2, ResourceType.MOUNTAIN to 1
            ), developmentCards = listOf(DevelopmentCard.KNIGHT, DevelopmentCard.ROAD_BUILDING,
                DevelopmentCard.MONOPOLY)),
            Player(2, "Joueur 2", Color(0xFF457B9D), 3, mapOf(
                ResourceType.PASTURE to 4, ResourceType.MOUNTAIN to 3
            ), developmentCards = listOf(DevelopmentCard.YEAR_OF_PLENTY)),
            Player(3, "Joueur 3", Color(0xFF2A9D8F), 2, mapOf(
                ResourceType.HILL to 3, ResourceType.FIELD to 4
            ), developmentCards = listOf(DevelopmentCard.VICTORY_POINT)),
        ),
        settlements = listOf(
            Settlement(1, 2, 2, 0),
            Settlement(1, 1, 1, 2, isCity = true),
            Settlement(2, 0, 1, 1),
            Settlement(2, 3, 2, 4),
            Settlement(3, 4, 1, 3)
        ),
        roads = listOf(
            Road(1, 2, 2, 0),
            Road(1, 1, 1, 2),
            Road(2, 0, 1, 1),
            Road(3, 4, 1, 3)
        )
    )
}
