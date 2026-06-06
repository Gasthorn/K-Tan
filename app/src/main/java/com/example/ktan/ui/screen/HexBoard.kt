package com.example.ktan.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktan.data.model.BoardState
import com.example.ktan.data.model.HexTile
import com.example.ktan.data.model.Road
import com.example.ktan.data.model.Settlement
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun HexBoard(
    state: BoardState,
    isLandscape: Boolean,
    onEdgeClick: (Int, Int, Int) -> Unit = { _, _, _ -> },
    onVertexClick: (Int, Int, Int) -> Unit = { _, _, _ -> },
    onTileClick: (Int, Int) -> Unit = { _, _ -> }
) {
    val textMeasurer = rememberTextMeasurer()
    val diceSum = state.diceResult?.let { it.first + it.second }
    val tiles = state.tiles
    val playersMap = state.players.associateBy { it.id }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        val availableWidth = constraints.maxWidth.toFloat()
        val availableHeight = constraints.maxHeight.toFloat()

        // ───────────── CALCULATE GEOMETRY ─────────────
        val geometry = remember(availableWidth, availableHeight, isLandscape, tiles) {
            calculateBoardGeometry(availableWidth, availableHeight, isLandscape, tiles)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state.phase, geometry) {
                    detectTapGestures { offset ->
                        // 1. If in Robber phase, prioritize tile centers
                        if (state.phase == com.example.ktan.data.model.GamePhase.ROBBER) {
                            geometry.tileCenters.forEach { (coord, center) ->
                                if ((offset - center).getDistance() < 80f) {
                                    onTileClick(coord.first, coord.second)
                                    return@detectTapGestures
                                }
                            }
                        }

                        // 2. Check vertices
                        geometry.vertexPoints.forEach { (key, pos) ->
                            if ((offset - pos).getDistance() < 25f) {
                                onVertexClick(key.first, key.second, key.third)
                                return@detectTapGestures
                            }
                        }
                        
                        // 3. Check edges
                        geometry.edgeLines.forEach { (key, points) ->
                            val (p1, p2) = points
                            if (distanceToSegment(offset, p1, p2) < 20f) {
                                onEdgeClick(key.first, key.second, key.third)
                                return@detectTapGestures
                            }
                        }

                        // 4. Regular tile click
                        if (state.phase != com.example.ktan.data.model.GamePhase.ROBBER) {
                            geometry.tileCenters.forEach { (coord, center) ->
                                if ((offset - center).getDistance() < 80f) {
                                    onTileClick(coord.first, coord.second)
                                    return@detectTapGestures
                                }
                            }
                        }
                    }
                }
        ) {
            // Draw Tiles
            geometry.tileData.forEach { data ->
                val isHighlighted = diceSum != null && data.tile.number == diceSum && !data.tile.hasRobber
                drawHexTile(data.tile, data.center.x, data.center.y, geometry.hexSize, textMeasurer, isHighlighted)
            }

            // Draw Roads
            state.roads.forEach { road ->
                val center = geometry.tileCenters[road.tileRow to road.tileCol] ?: return@forEach
                val p1 = getHexVertex(center.x, center.y, geometry.hexSize, road.edgeIndex)
                val p2 = getHexVertex(center.x, center.y, geometry.hexSize, (road.edgeIndex + 1) % 6)
                val color = playersMap[road.playerId]?.color ?: Color.Gray
                drawLine(color = color, start = p1, end = p2, strokeWidth = 8f, cap = StrokeCap.Round)
            }

            // Draw Settlements
            state.settlements.forEach { settlement ->
                val center = geometry.tileCenters[settlement.tileRow to settlement.tileCol] ?: return@forEach
                val pos = getHexVertex(center.x, center.y, geometry.hexSize, settlement.vertexIndex)
                val color = playersMap[settlement.playerId]?.color ?: Color.Gray
                if (settlement.isCity) {
                    drawRect(color = color, topLeft = Offset(pos.x - 12f, pos.y - 12f), size = androidx.compose.ui.geometry.Size(24f, 24f))
                } else {
                    drawCircle(color = color, radius = 10f, center = pos)
                }
                drawCircle(color = Color.Black, radius = 10f, center = pos, style = Stroke(width = 2f))
            }
        }
    }
}

data class BoardGeometry(
    val hexSize: Float,
    val tileCenters: Map<Pair<Int, Int>, Offset>,
    val tileData: List<TileDrawData>,
    val edgeLines: Map<Triple<Int, Int, Int>, Pair<Offset, Offset>>,
    val vertexPoints: Map<Triple<Int, Int, Int>, Offset>
)

data class TileDrawData(val tile: HexTile, val center: Offset)

private fun calculateBoardGeometry(w: Float, h: Float, isLandscape: Boolean, tiles: List<HexTile>): BoardGeometry {
    val rowSizes = listOf(3, 4, 5, 4, 3)
    val rowOffsets = listOf(1f, 0.5f, 0f, 0.5f, 1f)
    
    val baseSize = minOf(w, h) / 8f
    val hexSize = if (isLandscape) baseSize * 1.045f else baseSize * 0.9f
    
    val hexW = hexSize * sqrt(3f)
    val hexH = hexSize * 2f
    val boardWidth = hexW * 5
    val boardHeight = hexH * 0.75f * 5 + hexH * 0.25f
    
    val startX = (w - boardWidth) / 2f + hexW / 2f
    val startY = (h - boardHeight) / 2f + hexH / 2f
    
    val tileCenters = mutableMapOf<Pair<Int, Int>, Offset>()
    val tileData = mutableListOf<TileDrawData>()
    val edgeLines = mutableMapOf<Triple<Int, Int, Int>, Pair<Offset, Offset>>()
    val vertexPoints = mutableMapOf<Triple<Int, Int, Int>, Offset>()
    
    var tileIndex = 0
    rowSizes.forEachIndexed { row, count ->
        val offsetX = rowOffsets[row] * hexW
        for (col in 0 until count) {
            val tile = tiles.getOrNull(tileIndex++) ?: continue
            val cx = startX + offsetX + col * hexW
            val cy = startY + row * hexH * 0.75f
            val center = Offset(cx, cy)
            
            tileCenters[row to col] = center
            tileData.add(TileDrawData(tile, center))
            
            for (i in 0..5) {
                val p1 = getHexVertex(cx, cy, hexSize, i)
                val p2 = getHexVertex(cx, cy, hexSize, (i + 1) % 6)
                edgeLines[Triple(row, col, i)] = Pair(p1, p2)
                vertexPoints[Triple(row, col, i)] = p1
            }
        }
    }
    
    return BoardGeometry(hexSize, tileCenters, tileData, edgeLines, vertexPoints)
}

fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
    val l2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)
    if (l2 == 0f) return (p - a).getDistance()
    var t = ((p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y)) / l2
    t = maxOf(0f, minOf(1f, t))
    return (p - Offset(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y))).getDistance()
}

fun DrawScope.drawHexTile(
    tile: HexTile,
    cx: Float,
    cy: Float,
    size: Float,
    textMeasurer: TextMeasurer,
    isHighlighted: Boolean = false
) {
    val path = hexPath(cx, cy, size - 2f)
    if (isHighlighted) {
        val highlightPath = hexPath(cx, cy, size + 4f)
        drawPath(highlightPath, color = Color.White.copy(alpha = 0.5f))
    }
    drawPath(path, color = tile.resource.color)
    val borderColor = if (isHighlighted) Color.White else Color(0xFF2E0A05)
    val borderWidth = if (isHighlighted) 6f else 2f
    drawPath(path, color = borderColor, style = Stroke(width = borderWidth))

    if (tile.hasRobber) {
        drawCircle(color = Color.Black.copy(alpha = 0.7f), radius = size * 0.4f, center = Offset(cx, cy))
        val textLayout = textMeasurer.measure(AnnotatedString("👤"), style = TextStyle(fontSize = (size * 0.4f).sp))
        drawText(textLayout, topLeft = Offset(cx - textLayout.size.width / 2f, cy - textLayout.size.height / 2f))
    }

    if (tile.number != null && !tile.hasRobber) {
        val tokenRadius = size * 0.28f
        val tokenColor = if (tile.number == 6 || tile.number == 8) Color(0xFFDC2626) else Color(0xFFF5F0E8)
        drawCircle(color = tokenColor, radius = tokenRadius, center = Offset(cx, cy))
        drawCircle(color = Color.Black.copy(alpha = 0.2f), radius = tokenRadius, center = Offset(cx, cy), style = Stroke(1f))
        val textColor = if (tile.number == 6 || tile.number == 8) Color.White else Color.Black
        val textLayout = textMeasurer.measure(
            text = AnnotatedString(tile.number.toString()),
            style = TextStyle(color = textColor, fontSize = (tokenRadius * 0.5f).sp, fontWeight = FontWeight.Bold)
        )
        drawText(textLayout, topLeft = Offset(cx - textLayout.size.width / 2f, cy - textLayout.size.height / 2f))
    }
}

fun hexPath(cx: Float, cy: Float, size: Float): Path {
    val path = Path()
    for (i in 0..5) {
        val angle = Math.toRadians((60.0 * i) - 30.0).toFloat()
        val x = cx + size * cos(angle)
        val y = cy + size * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

fun getHexVertex(cx: Float, cy: Float, size: Float, index: Int): Offset {
    val angle = Math.toRadians((60.0 * index) - 30.0).toFloat()
    return Offset(cx + size * cos(angle), cy + size * sin(angle))
}
