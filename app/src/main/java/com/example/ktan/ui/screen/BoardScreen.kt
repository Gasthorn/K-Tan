package com.example.ktan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktan.data.model.GamePhase
import com.example.ktan.data.model.ResourceType
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(viewModel: GameViewModel = viewModel(), onQuit: () -> Unit) {
    var showTradeDialog by rememberSaveable { mutableStateOf(false) }
    var showBuildDialog by rememberSaveable { mutableStateOf(false) }
    var showDevCardsDialog by rememberSaveable { mutableStateOf(false) }

    val localState = viewModel.state
    val currentPlayer = localState.players[localState.currentPlayerIndex]
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color(0xFF5D1D09),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Tour de : ${currentPlayer.name}",
                            color = Color(0xFFF1C40F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = currentPlayer.color.copy(alpha = 0.9f)
                    ),
                    actions = {
                        OutlinedButton(
                            onClick = onQuit,
                            border = BorderStroke(1.dp, Color(0xFFF1C40F)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("MENU", color = Color(0xFFF1C40F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "⭐ ${currentPlayer.victoryPoints} pts",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color(0xFFF1C40F),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isLandscape) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // LEFT: Scoreboard
                        Box(modifier = Modifier.weight(1.5f).fillMaxHeight()) {
                            ScoreboardRow(
                                players = localState.players,
                                currentIndex = localState.currentPlayerIndex,
                                isLandscape = true
                            )
                        }

                        // CENTER: HexBoard
                        Box(
                            modifier = Modifier.weight(3.0f).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            HexBoard(
                                state = localState,
                                isLandscape = true,
                                onEdgeClick = { r, c, e ->
                                    if (viewModel.isBuildingRoad) viewModel.buildRoad(r, c, e)
                                },
                                onVertexClick = { r, c, v ->
                                    if (viewModel.isBuildingSettlement) viewModel.buildSettlement(r, c, v)
                                    else if (viewModel.isBuildingCity) viewModel.buildCity(r, c, v)
                                },
                                onTileClick = { r, c ->
                                    if (localState.phase == GamePhase.ROBBER) viewModel.moveRobber(r, c)
                                }
                            )
                        }

                        // RIGHT: Hand + Actions
                        Column(
                            modifier = Modifier.weight(2.5f).fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            ResourceHand(player = currentPlayer)
                            
                            if (localState.phase == GamePhase.PLACEMENT) {
                                Box(modifier = Modifier.padding(8.dp)) { PlacementInstructions() }
                            } else {
                                BottomActionBar(
                                    diceResult = localState.diceResult,
                                    hasRolled = localState.diceResult != null,
                                    onRollDice = { viewModel.rollDice() },
                                    onTrade = { showTradeDialog = true },
                                    onBuild = { showBuildDialog = true },
                                    onDevCards = { showDevCardsDialog = true },
                                    onEndTurn = { viewModel.nextTurn() }
                                )
                            }
                        }
                    }
                } else {
                    // PORTRAIT
                    ScoreboardRow(
                        players = localState.players,
                        currentIndex = localState.currentPlayerIndex,
                        isLandscape = false
                    )

                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        HexBoard(
                            state = localState,
                            isLandscape = false,
                            onEdgeClick = { r, c, e ->
                                if (viewModel.isBuildingRoad) viewModel.buildRoad(r, c, e)
                            },
                            onVertexClick = { r, c, v ->
                                if (viewModel.isBuildingSettlement) viewModel.buildSettlement(r, c, v)
                                else if (viewModel.isBuildingCity) viewModel.buildCity(r, c, v)
                            },
                            onTileClick = { r, c ->
                                if (localState.phase == GamePhase.ROBBER) viewModel.moveRobber(r, c)
                            }
                        )
                    }

                    ResourceHand(player = currentPlayer)

                    if (localState.phase == GamePhase.PLACEMENT) {
                        PlacementInstructions()
                    } else {
                        BottomActionBar(
                            diceResult = localState.diceResult,
                            hasRolled = localState.diceResult != null,
                            onRollDice = { viewModel.rollDice() },
                            onTrade = { showTradeDialog = true },
                            onBuild = { showBuildDialog = true },
                            onDevCards = { showDevCardsDialog = true },
                            onEndTurn = { viewModel.nextTurn() }
                        )
                    }
                }
            }
        }

        // Overlay for Special Modes
        val modeInfo = when {
            viewModel.freeRoadsAvailable > 0 -> "CARTE DÉV : Placez encore ${viewModel.freeRoadsAvailable} route(s) gratuite(s)"
            viewModel.isBuildingRoad -> if (localState.phase == GamePhase.PLACEMENT) "PHASE INITIALE : Placez votre route" else "Construction de Route"
            viewModel.isBuildingSettlement -> if (localState.phase == GamePhase.PLACEMENT) "PHASE INITIALE : Placez votre village" else "Construction de Village"
            viewModel.isBuildingCity -> "Construction de Ville"
            localState.phase == GamePhase.ROBBER -> "LE VOLEUR : Cliquez sur une tuile pour le déplacer"
            else -> null
        }

        if (modeInfo != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp) // Position right below the TopAppBar
                    .padding(horizontal = 16.dp),
                color = if (localState.phase == GamePhase.ROBBER) Color(0xFF991B1B) else Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modeInfo, 
                        color = Color(0xFFF1C40F), 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    if (localState.phase != GamePhase.ROBBER && localState.phase != GamePhase.PLACEMENT && viewModel.freeRoadsAvailable == 0) {
                        Spacer(Modifier.width(12.dp))
                        Button(
                            onClick = { viewModel.stopBuilding() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                        ) { Text("Annuler", fontSize = 10.sp, color = Color.White) }
                    }
                }
            }
        }
    }

    // DIALOGS
    if (showTradeDialog) {
        TradeDialog(
            currentPlayer = currentPlayer,
            otherPlayers = localState.players.filter { it.id != currentPlayer.id },
            onDismiss = { showTradeDialog = false },
            onExecuteTrade = { target, offer, request -> viewModel.proposeTrade(target, offer, request) }
        )
    }

    viewModel.pendingTrade?.let { trade ->
        val proposer = localState.players.find { it.id == trade.proposerId }
        val receiver = localState.players.find { it.id == trade.receiverId }
        if (proposer != null && receiver != null) {
            TradeApprovalDialog(
                proposerName = proposer.name,
                receiverName = receiver.name,
                offered = trade.offered,
                requested = trade.requested,
                onAccept = { viewModel.acceptTrade() },
                onReject = { viewModel.rejectTrade() }
            )
        }
    }

    if (showBuildDialog) {
        BuildDialog(
            playerResources = currentPlayer.resources,
            onDismiss = { showBuildDialog = false },
            onBuildItem = { item ->
                when (item.name) {
                    "Route" -> viewModel.startBuildingRoad()
                    "Village" -> viewModel.startBuildingSettlement()
                    "Ville" -> viewModel.startBuildingCity()
                    "Carte Dév." -> viewModel.buyDevelopmentCard()
                }
            }
        )
    }

    if (showDevCardsDialog) {
        DevelopmentCardsDialog(
            cards = currentPlayer.developmentCards,
            onPlayCard = { card -> viewModel.playDevelopmentCard(card) },
            onDismiss = { showDevCardsDialog = false }
        )
    }

    if (viewModel.showMonopolySelection) {
        MonopolyDialog(
            onSelect = { res -> viewModel.applyMonopoly(res) },
            onDismiss = { viewModel.cancelMonopoly() }
        )
    }
}

@Composable
fun TradeApprovalDialog(
    proposerName: String,
    receiverName: String,
    offered: Map<ResourceType, Int>,
    requested: Map<ResourceType, Int>,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onReject,
        containerColor = Color(0xFF7B241C),
        title = { Text("Proposition d'échange", color = Color(0xFFF1C40F), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("$proposerName propose un échange à $receiverName :", color = Color(0xFFF1C40F))
                Text("Il vous donne :", color = Color(0xFF86EFAC), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    offered.filter { it.value > 0 }.forEach { (res, qty) ->
                        Text("${res.emoji} x$qty", color = Color(0xFFF1C40F))
                    }
                }
                Text("Il demande :", color = Color(0xFF93C5FD), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    requested.filter { it.value > 0 }.forEach { (res, qty) ->
                        Text("${res.emoji} x$qty", color = Color(0xFFF1C40F))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))) { Text("Accepter") }
        },
        dismissButton = {
            OutlinedButton(onClick = onReject, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("Refuser") }
        }
    )
}

@Composable
fun PlacementInstructions() {
    Surface(
        color = Color.Black.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Phase de placement initiale", color = Color(0xFFF1C40F), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Placez vos deux premiers villages et routes gratuitement. Le second village vous donnera vos ressources de départ.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
