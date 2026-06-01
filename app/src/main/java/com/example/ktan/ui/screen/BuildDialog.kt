package com.example.ktan.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktan.data.model.ResourceType

data class BuildableItem(
    val name: String,
    val emoji: String,
    val cost: Map<ResourceType, Int>
)

val BUILDABLES = listOf(
    BuildableItem("Route", "🛤️", mapOf(ResourceType.FOREST to 1, ResourceType.HILL to 1)),
    BuildableItem("Village", "🏘️", mapOf(
        ResourceType.FOREST to 1, ResourceType.PASTURE to 1,
        ResourceType.FIELD to 1, ResourceType.HILL to 1
    )),
    BuildableItem("Ville", "🏙️", mapOf(ResourceType.FIELD to 2, ResourceType.MOUNTAIN to 3)),
    BuildableItem("Carte Dév.", "🃏", mapOf(
        ResourceType.PASTURE to 1, ResourceType.MOUNTAIN to 1, ResourceType.FIELD to 1
    ))
)

@Composable
fun BuildDialog(
    playerResources: Map<ResourceType, Int>,
    onDismiss: () -> Unit,
    onBuildItem: (BuildableItem) -> Unit
) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF7B241C),
        title = {
            Text("Construire", color = Color(0xFFF1C40F), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BUILDABLES.forEach { item ->
                    val canAfford = item.cost.all { (res, qty) ->
                        (playerResources[res] ?: 0) >= qty
                    }
                    BuildableRow(
                        item = item,
                        canAfford = canAfford,
                        onBuild = {
                            onBuildItem(item)
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF1C40F))
            ) { Text("Fermer") }
        }
    )
}

@Composable
fun BuildableRow(item: BuildableItem, canAfford: Boolean, onBuild: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (canAfford) Color(0xFF922B21) else Color(0xFF4A100B),
        border = if (canAfford) BorderStroke(1.dp, Color(0xFFF1C40F)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.emoji, fontSize = 22.sp)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    item.cost.forEach { (res, qty) ->
                        Text(
                            "${res.emoji}×$qty",
                            fontSize = 11.sp,
                            color = Color.White.copy(0.9f)
                        )
                    }
                }
            }
            if (canAfford) {
                Button(
                    onClick = onBuild,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Construire", fontSize = 11.sp)
                }
            } else {
                Text(
                    "Insuffisant",
                    color = Color(0xFFF1948A),
                    fontSize = 11.sp
                )
            }
        }
    }
}
