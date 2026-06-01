package com.example.ktan.data.model

import androidx.compose.ui.graphics.Color

data class Player(
    val id: Int,
    val name: String,
    val color: Color,
    val victoryPoints: Int,
    val resources: Map<ResourceType, Int>,
    val developmentCards: List<DevelopmentCard> = emptyList(),
    val knightsPlayed: Int = 0,
    val hasLongestRoad: Boolean = false,
    val hasLargestArmy: Boolean = false
)
