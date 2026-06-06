package com.example.ktan.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktan.data.model.Player
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun ScoreboardRow(players: List<Player>, currentIndex: Int, isLandscape: Boolean) {
    val configuration = LocalConfiguration.current
    val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val themeBrown = Color(0xFF2E0A05)
    val catanYellow = Color(0xFFF1C40F)

    if (landscape) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(themeBrown)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            players.forEachIndexed { index, player ->
                val isActive = index == currentIndex
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isActive) player.color else player.color.copy(alpha = 0.2f),
                    border = if (isActive) BorderStroke(2.dp, catanYellow) else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            player.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White else catanYellow.copy(alpha = 0.8f),
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "⭐ ${player.victoryPoints}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = catanYellow
                        )
                    }
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(themeBrown)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            players.forEachIndexed { index, player ->
                val isActive = index == currentIndex
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isActive) player.color else player.color.copy(alpha = 0.2f),
                    border = if (isActive) BorderStroke(2.dp, catanYellow) else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            player.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White else catanYellow.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Text(
                            "⭐ ${player.victoryPoints}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = catanYellow
                        )
                    }
                }
            }
        }
    }
}
