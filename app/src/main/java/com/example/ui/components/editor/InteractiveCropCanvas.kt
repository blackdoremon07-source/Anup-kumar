package com.example.ui.components.editor

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.model.NormalizedCropRect
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron
import kotlin.math.max
import kotlin.math.min

@Composable
fun InteractiveCropCanvas(
    bitmap: Bitmap,
    initialCropRect: NormalizedCropRect,
    onApplyCrop: (NormalizedCropRect) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cropLeft by remember { mutableStateOf(initialCropRect.left) }
    var cropTop by remember { mutableStateOf(initialCropRect.top) }
    var cropRight by remember { mutableStateOf(initialCropRect.right) }
    var cropBottom by remember { mutableStateOf(initialCropRect.bottom) }

    val imageRatio = if (bitmap.height > 0) bitmap.width.toFloat() / bitmap.height.toFloat() else 1f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Interactive Crop / क्रॉप सम्पादक",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Drag corners or center to adjust",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas containing the bitmap and interactive drag handles
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageRatio.coerceIn(0.5f, 2.0f))
                    .background(Color.Black)
            ) {
                val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val canvasW = size.width.toFloat()
                                val canvasH = size.height.toFloat()
                                if (canvasW <= 0f || canvasH <= 0f) return@detectDragGestures

                                val normDx = dragAmount.x / canvasW
                                val normDy = dragAmount.y / canvasH

                                val touchX = change.position.x / canvasW
                                val touchY = change.position.y / canvasH

                                val nearLeft = kotlin.math.abs(touchX - cropLeft) < 0.2f
                                val nearRight = kotlin.math.abs(touchX - cropRight) < 0.2f
                                val nearTop = kotlin.math.abs(touchY - cropTop) < 0.2f
                                val nearBottom = kotlin.math.abs(touchY - cropBottom) < 0.2f

                                if (nearLeft && nearTop) {
                                    cropLeft = (cropLeft + normDx).coerceIn(0f, cropRight - 0.1f)
                                    cropTop = (cropTop + normDy).coerceIn(0f, cropBottom - 0.1f)
                                } else if (nearRight && nearBottom) {
                                    cropRight = (cropRight + normDx).coerceIn(cropLeft + 0.1f, 1f)
                                    cropBottom = (cropBottom + normDy).coerceIn(cropTop + 0.1f, 1f)
                                } else if (nearRight && nearTop) {
                                    cropRight = (cropRight + normDx).coerceIn(cropLeft + 0.1f, 1f)
                                    cropTop = (cropTop + normDy).coerceIn(0f, cropBottom - 0.1f)
                                } else if (nearLeft && nearBottom) {
                                    cropLeft = (cropLeft + normDx).coerceIn(0f, cropRight - 0.1f)
                                    cropBottom = (cropBottom + normDy).coerceIn(cropTop + 0.1f, 1f)
                                } else {
                                    // Move entire window if inside crop area
                                    val w = cropRight - cropLeft
                                    val h = cropBottom - cropTop
                                    val newL = (cropLeft + normDx).coerceIn(0f, 1f - w)
                                    val newT = (cropTop + normDy).coerceIn(0f, 1f - h)
                                    cropLeft = newL
                                    cropRight = newL + w
                                    cropTop = newT
                                    cropBottom = newT + h
                                }
                            }
                        }
                ) {
                    val canvasW = size.width
                    val canvasH = size.height

                    // 1. Draw source image scaled to fill
                    drawImage(
                        image = imageBitmap,
                        dstOffset = IntOffset.Zero,
                        dstSize = IntSize(canvasW.toInt(), canvasH.toInt())
                    )

                    // 2. Dimmed surrounding scrim
                    val rectL = cropLeft * canvasW
                    val rectT = cropTop * canvasH
                    val rectR = cropRight * canvasW
                    val rectB = cropBottom * canvasH

                    val scrimColor = Color(0x99000000)

                    // Top
                    drawRect(scrimColor, Offset(0f, 0f), Size(canvasW, rectT))
                    // Bottom
                    drawRect(scrimColor, Offset(0f, rectB), Size(canvasW, canvasH - rectB))
                    // Left
                    drawRect(scrimColor, Offset(0f, rectT), Size(rectL, rectB - rectT))
                    // Right
                    drawRect(scrimColor, Offset(rectR, rectT), Size(canvasW - rectR, rectB - rectT))

                    // 3. Crop outline & Rule-of-thirds grid
                    val cropW = rectR - rectL
                    val cropH = rectB - rectT

                    drawRect(
                        color = DgSaffron,
                        topLeft = Offset(rectL, rectT),
                        size = Size(cropW, cropH),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Grid lines
                    val thirdW = cropW / 3f
                    val thirdH = cropH / 3f
                    val gridStroke = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    val gridColor = Color(0x80FFFFFF)

                    drawLine(gridColor, Offset(rectL + thirdW, rectT), Offset(rectL + thirdW, rectB), strokeWidth = 1f)
                    drawLine(gridColor, Offset(rectL + 2 * thirdW, rectT), Offset(rectL + 2 * thirdW, rectB), strokeWidth = 1f)
                    drawLine(gridColor, Offset(rectL, rectT + thirdH), Offset(rectR, rectT + thirdH), strokeWidth = 1f)
                    drawLine(gridColor, Offset(rectL, rectT + 2 * thirdH), Offset(rectR, rectT + 2 * thirdH), strokeWidth = 1f)

                    // 4. Corner handles
                    val handleLen = 22.dp.toPx()
                    val handleStroke = Stroke(width = 4.dp.toPx())
                    val handleColor = Color.White

                    // Top-Left
                    drawLine(handleColor, Offset(rectL - 2, rectT), Offset(rectL + handleLen, rectT), strokeWidth = handleStroke.width)
                    drawLine(handleColor, Offset(rectL, rectT - 2), Offset(rectL, rectT + handleLen), strokeWidth = handleStroke.width)
                    // Top-Right
                    drawLine(handleColor, Offset(rectR + 2, rectT), Offset(rectR - handleLen, rectT), strokeWidth = handleStroke.width)
                    drawLine(handleColor, Offset(rectR, rectT - 2), Offset(rectR, rectT + handleLen), strokeWidth = handleStroke.width)
                    // Bottom-Left
                    drawLine(handleColor, Offset(rectL - 2, rectB), Offset(rectL + handleLen, rectB), strokeWidth = handleStroke.width)
                    drawLine(handleColor, Offset(rectL, rectB + 2), Offset(rectL, rectB - handleLen), strokeWidth = handleStroke.width)
                    // Bottom-Right
                    drawLine(handleColor, Offset(rectR + 2, rectB), Offset(rectR - handleLen, rectB), strokeWidth = handleStroke.width)
                    drawLine(handleColor, Offset(rectR, rectB + 2), Offset(rectR, rectB - handleLen), strokeWidth = handleStroke.width)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Apply Crop, Cancel, Reset, Center Crop
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        cropLeft = 0f
                        cropTop = 0f
                        cropRight = 1f
                        cropBottom = 1f
                    },
                    modifier = Modifier.weight(1f).testTag("crop_reset_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        val w = cropRight - cropLeft
                        val h = cropBottom - cropTop
                        cropLeft = (1f - w) / 2f
                        cropRight = cropLeft + w
                        cropTop = (1f - h) / 2f
                        cropBottom = cropTop + h
                    },
                    modifier = Modifier.weight(1f).testTag("crop_center_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.FilterCenterFocus, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Center", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).testTag("crop_cancel_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171))
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancel", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        onApplyCrop(
                            NormalizedCropRect(
                                left = cropLeft,
                                top = cropTop,
                                right = cropRight,
                                bottom = cropBottom
                            )
                        )
                    },
                    modifier = Modifier.weight(1.2f).testTag("crop_apply_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DgEmerald)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply Crop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
