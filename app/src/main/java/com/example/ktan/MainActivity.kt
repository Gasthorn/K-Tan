package com.example.ktan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktan.ui.screen.BoardScreen
import com.example.ktan.ui.screen.GameViewModel
import com.example.ktan.ui.screen.StartScreen
import com.example.ktan.ui.screen.OnlineLobbyScreen

enum class GameScreen { START, BOARD, ONLINE_LOBBY }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val viewModel: GameViewModel = viewModel()
                var currentScreen by rememberSaveable { mutableStateOf(GameScreen.START) }

                when (currentScreen) {
                    GameScreen.START -> {
                        StartScreen(onStartGame = { mode, count ->
                            if (mode == "online") {
                                currentScreen = GameScreen.ONLINE_LOBBY
                            } else {
                                viewModel.startGame(mode, count)
                                currentScreen = GameScreen.BOARD
                            }
                        })
                    }
                    GameScreen.BOARD -> {
                        BoardScreen(viewModel = viewModel, onQuit = { currentScreen = GameScreen.START })
                    }
                    GameScreen.ONLINE_LOBBY -> {
                        OnlineLobbyScreen(
                            onBack = { currentScreen = GameScreen.START },
                            onJoinRoom = { _ ->
                                // For now, just start a classic game as a mock for joining
                                viewModel.startGame("classic", 3)
                                currentScreen = GameScreen.BOARD
                            }
                        )
                    }
                }
            }
        }
    }
}
