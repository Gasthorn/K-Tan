package com.example.ktan.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun StartScreen(onStartGame: (String, Int) -> Unit) {
    var playerCount by rememberSaveable { mutableIntStateOf(3) }
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF5D1D09), Color(0xFF2E0A05))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "K'TAN",
                        color = Color(0xFFF1C40F),
                        fontSize = 70.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.displayLarge
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.width(300.dp)
                    ) {
                        ModeSelector(
                            playerCount = playerCount,
                            expanded = expanded,
                            onExpandRequest = { expanded = it },
                            onPlayerCountChange = { playerCount = it },
                            onStartGame = onStartGame
                        )
                    }
                }
            } else {
                Text(
                    text = "K'TAN",
                    color = Color(0xFFF1C40F),
                    fontSize = 100.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.displayLarge
                )

                Spacer(modifier = Modifier.height(48.dp))

                ModeSelector(
                    playerCount = playerCount,
                    expanded = expanded,
                    onExpandRequest = { expanded = it },
                    onPlayerCountChange = { playerCount = it },
                    onStartGame = onStartGame
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    "Un jeu de conquête et de ressources",
                    color = Color(0xFFF1C40F).copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ModeSelector(
    playerCount: Int,
    expanded: Boolean,
    onExpandRequest: (Boolean) -> Unit,
    onPlayerCountChange: (Int) -> Unit,
    onStartGame: (String, Int) -> Unit
) {
    val buttonColor = Color(0xFFD35400)
    val textColor = Color(0xFFF1C40F)

    Text(
        "CHOISISSEZ VOTRE MODE :",
        color = textColor.copy(alpha = 0.8f),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(16.dp))

    Box {
        Button(
            onClick = { onExpandRequest(true) },
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = textColor
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, textColor)
        ) {
            Text("MODE CLASSIQUE ($playerCount JOUEURS) ▾", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandRequest(false) },
            modifier = Modifier
                .width(280.dp)
                .background(Color(0xFF5D1D09))
        ) {
            DropdownMenuItem(
                text = { Text("3 JOUEURS", color = textColor, fontWeight = FontWeight.Bold) },
                onClick = {
                    onPlayerCountChange(3)
                    onExpandRequest(false)
                }
            )
            DropdownMenuItem(
                text = { Text("4 JOUEURS", color = textColor, fontWeight = FontWeight.Bold) },
                onClick = {
                    onPlayerCountChange(4)
                    onExpandRequest(false)
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = { onStartGame("classic", playerCount) },
        modifier = Modifier
            .width(280.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor)
    ) {
        Text("COMMENCER LA PARTIE", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }

    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(modifier = Modifier.width(200.dp), color = Color.White.copy(alpha = 0.1f))
    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = { onStartGame("demo", 3) },
        modifier = Modifier
            .width(280.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor)
    ) {
        Text("DÉMONSTRATION", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = { onStartGame("tutorial", 1) },
        modifier = Modifier
            .width(280.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor)
    ) {
        Text("TUTORIEL", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = { onStartGame("online", 3) },
        modifier = Modifier
            .width(280.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor)
    ) {
        Text("🌍 EN LIGNE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
