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

@Composable
fun ResourceHand(player: Player) {
    val allResources = ResourceType.entries.filter { it != ResourceType.DESERT }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E0A05))
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "MAIN :",
            color = Color(0xFFF1C40F).copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(end = 2.dp)
        )
        allResources.forEach { res ->
            val count = player.resources[res] ?: 0
            ResourceCard(resource = res, count = count)
        }
    }
}

@Composable
fun ResourceCard(resource: ResourceType, count: Int) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (count > 0) resource.color else resource.color.copy(alpha = 0.2f),
        modifier = Modifier.size(36.dp, 44.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(resource.emoji, fontSize = 14.sp)
            Text(
                "$count",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (count > 0) Color(0xFFF1C40F) else Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
