package com.example.ktan.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktan.data.model.Player
import com.example.ktan.data.model.ResourceType

@Composable
fun TradeDialog(
    currentPlayer: Player,
    otherPlayers: List<Player>,
    onDismiss: () -> Unit,
    onExecuteTrade: (Player, Map<ResourceType, Int>, Map<ResourceType, Int>) -> Unit
) {
    val resources = ResourceType.entries.filter { it != ResourceType.DESERT }
    var offerCounts by remember { mutableStateOf(resources.associateWith { 0 }) }
    var requestCounts by remember { mutableStateOf(resources.associateWith { 0 }) }
    var selectedPlayer by remember { mutableStateOf(otherPlayers.firstOrNull()) }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF7B241C),
        title = {
            Text("Proposer un échange", color = Color(0xFFF1C40F), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Target player selector
                Text("Avec :", color = Color(0xFFF1C40F).copy(0.8f), fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    otherPlayers.forEach { p ->
                        FilterChip(
                            selected = selectedPlayer == p,
                            onClick = { selectedPlayer = p },
                            label = { Text(p.name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = p.color,
                                selectedLabelColor = Color.White,
                                containerColor = p.color.copy(0.2f),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(0.15f))

                // Offer row
                Text("Vous donnez :", color = Color(0xFF86EFAC), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                ResourceCounterRow(resources, offerCounts, currentPlayer.resources) { res, delta ->
                    val max = currentPlayer.resources[res] ?: 0
                    val current = offerCounts[res] ?: 0
                    offerCounts = offerCounts + (res to (current + delta).coerceIn(0, max))
                }

                HorizontalDivider(color = Color.White.copy(0.15f))

                // Request row
                Text("Vous recevez :", color = Color(0xFF93C5FD), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                
                selectedPlayer?.let { p ->
                    Text("Il possède : ${p.resources.filter { it.value > 0 }.map { "${it.key.emoji}${it.value}" }.joinToString(" ")}", 
                        fontSize = 10.sp, color = Color.White.copy(0.7f))
                }

                ResourceCounterRow(resources, requestCounts, selectedPlayer?.resources) { res, delta ->
                    val current = requestCounts[res] ?: 0
                    val max = selectedPlayer?.resources?.get(res) ?: 0
                    requestCounts = requestCounts + (res to (current + delta).coerceIn(0, max))
                }
            }
        },
        confirmButton = {
            val hasOffer = offerCounts.values.any { it > 0 }
            val hasRequest = requestCounts.values.any { it > 0 }
            
            Button(
                onClick = {
                    selectedPlayer?.let { target ->
                        onExecuteTrade(target, offerCounts, requestCounts)
                    }
                    onDismiss()
                },
                enabled = hasOffer && hasRequest && selectedPlayer != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF16A34A),
                    contentColor = Color.White
                )
            ) { Text("Échanger") }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF1C40F))
            ) { Text("Annuler") }
        }
    )
}

@Composable
fun ResourceCounterRow(
    resources: List<ResourceType>,
    counts: Map<ResourceType, Int>,
    maxMap: Map<ResourceType, Int>?,
    onDelta: (ResourceType, Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        resources.forEach { res ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(res.emoji, fontSize = 16.sp)
                Text(
                    "${counts[res] ?: 0}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Row {
                    SmallIconButton("-") { onDelta(res, -1) }
                    SmallIconButton("+") { onDelta(res, +1) }
                }
                if (maxMap != null) {
                    Text(
                        "/${maxMap[res] ?: 0}",
                        color = Color.White.copy(0.6f),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SmallIconButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(Color.White.copy(0.2f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
