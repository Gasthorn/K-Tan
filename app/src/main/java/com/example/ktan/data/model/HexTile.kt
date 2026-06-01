package com.example.ktan.data.model

data class HexTile(
    val col: Int,
    val row: Int,
    val resource: ResourceType,
    val number: Int? = null,    // null pour desert
    val hasRobber: Boolean = false
)