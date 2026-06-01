package com.example.ktan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktan.data.model.DevelopmentCard

@Composable
fun DevelopmentCardsDialog(
    cards: List<DevelopmentCard>,
    onPlayCard: (DevelopmentCard) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF7B241C),
        title = {
            Text("Vos Cartes de Développement", color = Color(0xFFF1C40F), fontWeight = FontWeight.Bold)
        },
        text = {
            if (cards.isEmpty()) {
                Text("Vous n'avez pas encore de cartes.", color = Color.White.copy(alpha = 0.7f))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cards) { card ->
                        DevelopmentCardRow(card = card, onPlay = {
                            onPlayCard(card)
                            onDismiss()
                        })
                    }
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
fun DevelopmentCardRow(card: DevelopmentCard, onPlay: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF922B21),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(card.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(card.label, color = Color(0xFFF1C40F), fontWeight = FontWeight.Bold)
                Text(card.description, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            }
            if (card != DevelopmentCard.VICTORY_POINT) {
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Jouer", fontSize = 12.sp)
                }
            }
        }
    }
}
