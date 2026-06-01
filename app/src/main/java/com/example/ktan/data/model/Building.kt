package com.example.ktan.data.model

data class Road(
    val playerId: Int,
    val tileRow: Int,
    val tileCol: Int,
    val edgeIndex: Int // 0 to 5
)

data class Settlement(
    val playerId: Int,
    val tileRow: Int,
    val tileCol: Int,
    val vertexIndex: Int, // 0 to 5
    val isCity: Boolean = false
)
