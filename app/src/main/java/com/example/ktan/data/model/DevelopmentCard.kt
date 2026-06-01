package com.example.ktan.data.model

enum class DevelopmentCard(val label: String, val emoji: String, val description: String) {
    KNIGHT("Chevalier", "⚔️", "Déplace le voleur et vole une ressource."),
    VICTORY_POINT("Point de Victoire", "📜", "Donne 1 point de victoire caché."),
    ROAD_BUILDING("Construction de routes", "🛤️", "Placez 2 routes gratuitement."),
    YEAR_OF_PLENTY("Invention", "🏺", "Prenez 2 ressources au hasard dans la banque."),
    MONOPOLY("Monopole", "💰", "Choisissez une ressource, tous les joueurs doivent vous donner toutes leurs cartes de ce type.")
}
