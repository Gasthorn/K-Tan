# K'Tan

Application Android développée en Kotlin dans le cadre des enseignements de Design UX et Développement Mobile.

## Présentation

K'Tan est une adaptation du célèbre jeu de stratégie Catan sur smartphone et dont le nom est inspiré du BDE 2026 de l'Ensim : K'valtek.

L'objectif est de permettre aux joueurs de profiter d'une expérience complète de Catan, grâce à plusieurs modes de jeu adaptés aux usages mobiles.

## Fonctionnalités prévues

### Modes de jeu

- Mode Solo contre des adversaires contrôlés par l'application
- Mode Multijoueur en temps réel

### Gestion de partie

- Génération aléatoire du plateau
- Placement initial des colonies et routes
- Lancer de dés
- Production de ressources
- Construction de routes, villages et villes
- Cartes développement
- Gestion du voleur
- Échanges entre joueurs
- Vérification automatique des conditions de victoire

## Technologies utilisées

- Kotlin
- Android Studio
- Jetpack Compose
- Architecture MVVM

## Structure du projet

```text
com.catanmobile
│
├── data
│   └── model
│
├── domain
│   └── game
│
├── ui
│   ├── screen
│   ├── component
│   └── viewmodel
│
└── MainActivity.kt
```

## État d'avancement

### Terminé

- [x] Création du projet Android
- [x] Modélisation des ressources
- [x] Modélisation des tuiles
- [x] Génération du plateau
- [x] Affichage du plateau hexagonal
- [x] Gestion des joueurs
- [x] Gestion des tours
- [x] Routes
- [x] Villages
- [x] Villes
- [x] Cartes développement

### En Cours
- [ ] Tutoriel

### À venir
- [ ] IA
- [ ] Multijoueur temps réel
- [ ] Multijoueur asynchrone

## Installation

### Prérequis

- Android Studio Hedgehog ou supérieur
- Android SDK 34+
- JDK 17

### Lancer le projet

1. Cloner le dépôt :

```bash
git clone https://github.com/Gasthorn/K-Tan.git
```

2. Ouvrir le projet dans Android Studio.

3. Synchroniser Gradle.

4. Lancer l'application sur :
   - un émulateur Android
   - ou un appareil physique.

## Objectifs pédagogiques

Ce projet vise à mettre en pratique :

- le développement Android natif
- Kotlin
- Jetpack Compose
- l'architecture MVVM
- les principes UX/UI
- la gestion d'état dans une application complexe
- le développement multijoueur

## Auteur

Gaspard Bourdas-Rougeault

4A Info IPS Ensim, Le Mans Université – Année universitaire 2025-2026
