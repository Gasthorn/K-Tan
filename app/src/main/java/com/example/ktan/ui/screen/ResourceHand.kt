package com.example.ktan.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktan.data.model.Player
import com.example.ktan.data.model.ResourceType

import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun ResourceHand(player: Player) {
    val allResources = ResourceType.entries.filter { it != ResourceType.DESERT }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    if (isLandscape) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: First 3 resources
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                allResources.take(3).forEach { res ->
                    val count = player.resources[res] ?: 0
                    ResourceCard(resource = res, count = count, isLandscape = true)
                }
            }
            // Row 2: Last 2 resources
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                allResources.drop(3).forEach { res ->
                    val count = player.resources[res] ?: 0
                    ResourceCard(resource = res, count = count, isLandscape = true)
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E0A05))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            allResources.forEach { res ->
                val count = player.resources[res] ?: 0
                ResourceCard(resource = res, count = count, isLandscape = false)
            }
        }
    }
}

@Composable
fun ResourceCard(resource: ResourceType, count: Int, isLandscape: Boolean) {
    val width = if (isLandscape) 42.dp else 50.dp
    val height = if (isLandscape) 52.dp else 64.dp
    val emojiSize = if (isLandscape) 18.sp else 22.sp
    val textSize = if (isLandscape) 15.sp else 18.sp

    Surface(
        shape = RoundedCornerShape(if (isLandscape) 8.dp else 10.dp),
        color = if (count > 0) resource.color else resource.color.copy(alpha = 0.2f),
        modifier = Modifier.size(width, height)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(resource.emoji, fontSize = emojiSize)
            Text(
                "$count",
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                color = if (count > 0) Color(0xFFF1C40F) else Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
