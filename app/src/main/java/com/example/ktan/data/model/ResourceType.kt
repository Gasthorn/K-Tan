package com.example.ktan.data.model

import androidx.compose.ui.graphics.Color

enum class ResourceType(val color: Color, val label: String, val emoji: String) {
    FOREST(Color(0xFF2D6A4F), "Forêt", "🌲"),
    PASTURE(Color(0xFF95D5B2), "Pâturage", "🐑"),
    FIELD(Color(0xFFD4A017), "Champ", "🌾"),
    HILL(Color(0xFFB5451B), "Colline", "🧱"),
    MOUNTAIN(Color(0xFF6B7280), "Montagne", "⛰️"),
    DESERT(Color(0xFFE9C46A), "Désert", "🏜️")
}