package com.example.ktan.ui.screen

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun BottomActionBar(
    diceResult: Pair<Int, Int>?,
    hasRolled: Boolean,
    onRollDice: () -> Unit,
    onTrade: () -> Unit,
    onBuild: () -> Unit,
    onDevCards: () -> Unit,
    onEndTurn: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Surface(
        color = Color(0xFF2E0A05),
        shadowElevation = 8.dp
    ) {

        if (isLandscape) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DiceArea(
                    diceResult = diceResult,
                    hasRolled = hasRolled,
                    onRoll = onRollDice,
                    modifier = Modifier.fillMaxWidth(),
                    isLandscape = true
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActionButton(
                        label = "Échanger",
                        icon = "🔄",
                        enabled = hasRolled,
                        onClick = onTrade,
                        modifier = Modifier.weight(1f)
                    )

                    ActionButton(
                        label = "Construire",
                        icon = "🏗️",
                        enabled = hasRolled,
                        onClick = onBuild,
                        modifier = Modifier.weight(1f)
                    )
                    
                    ActionButton(
                        label = "Cartes",
                        icon = "🃏",
                        enabled = true,
                        onClick = onDevCards,
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = onEndTurn,
                    enabled = hasRolled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF374151)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("✅ Fin de tour", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DiceArea(
                    diceResult = diceResult,
                    hasRolled = hasRolled,
                    onRoll = onRollDice,
                    modifier = Modifier.weight(0.9f)
                )

                Column(
                    modifier = Modifier.weight(2.5f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ActionButton(
                            label = "Échange",
                            icon = "🔄",
                            enabled = hasRolled,
                            onClick = onTrade,
                            modifier = Modifier.weight(1f)
                        )

                        ActionButton(
                            label = "Bâtir",
                            icon = "🏗️",
                            enabled = hasRolled,
                            onClick = onBuild,
                            modifier = Modifier.weight(1f)
                        )
                        
                        ActionButton(
                            label = "Cartes",
                            icon = "🃏",
                            enabled = true,
                            onClick = onDevCards,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = onEndTurn,
                        enabled = hasRolled,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF16A34A),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF374151)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("✅ Fin de tour", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DiceArea(
    diceResult: Pair<Int, Int>?,
    hasRolled: Boolean,
    onRoll: () -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dice_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = if (!hasRolled) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (diceResult != null) {
            if (isLandscape) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        DieFace(value = diceResult.first)
                        DieFace(value = diceResult.second)
                    }
                    Text(
                        "= ${diceResult.first + diceResult.second}",
                        color = if (diceResult.first + diceResult.second == 7) Color(0xFFEF4444)
                        else Color(0xFFF1C40F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    DieFace(value = diceResult.first)
                    DieFace(value = diceResult.second)
                }
                Text(
                    "= ${diceResult.first + diceResult.second}",
                    color = if (diceResult.first + diceResult.second == 7) Color(0xFFEF4444)
                    else Color(0xFFF1C40F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        } else {
            Button(
                onClick = onRoll,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD97706),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎲 Lancer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DieFace(value: Int) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFF5F0E8),
        modifier = Modifier.size(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                diceFace(value),
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}

fun diceFace(n: Int) = when (n) {
    1 -> "⚀"; 2 -> "⚁"; 3 -> "⚂"; 4 -> "⚃"; 5 -> "⚄"; else -> "⚅"
}

@Composable
fun ActionButton(
    label: String,
    icon: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFF1C40F),
            disabledContentColor = Color(0xFFF1C40F).copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, if (enabled) Color(0xFFF1C40F) else Color.White.copy(0.15f))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 14.sp)
            Text(label, fontSize = 10.sp)
        }
    }
}
