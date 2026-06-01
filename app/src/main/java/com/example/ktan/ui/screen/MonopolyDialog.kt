package com.example.ktan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktan.data.model.ResourceType

@Composable
fun MonopolyDialog(
    onSelect: (ResourceType) -> Unit,
    onDismiss: () -> Unit
) {
    val resources = ResourceType.entries.filter { it != ResourceType.DESERT }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF7B241C),
        title = {
            Text("Monopole", color = Color(0xFFF1C40F), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Choisissez une ressource à monopoliser. Tous les joueurs devront vous donner toutes leurs cartes de ce type.", 
                    color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    resources.forEach { res ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = { onSelect(res) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text(res.emoji, fontSize = 32.sp)
                            }
                            Text(res.label, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF1C40F))
            ) { Text("Annuler") }
        }
    )
}
