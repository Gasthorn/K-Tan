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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val viewModel: GameViewModel = viewModel()
                var gameStarted by rememberSaveable { mutableStateOf(false) }

                if (!gameStarted) {
                    StartScreen(onStartGame = { mode, count ->
                        viewModel.startGame(mode, count)
                        gameStarted = true
                    })
                } else {
                    BoardScreen(viewModel = viewModel, onQuit = { gameStarted = false })
                }
            }
        }
    }
}
