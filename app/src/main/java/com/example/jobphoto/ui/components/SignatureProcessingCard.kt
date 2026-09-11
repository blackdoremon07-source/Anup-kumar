package com.example.jobphoto.ui.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobphoto.model.SignatureRequirement
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavy
import com.example.ui.theme.DgSaffron

@Composable
fun SignatureProcessingCard(
    originalBitmap: Bitmap?,
    processedBitmap: Bitmap?,
    processedBytes: ByteArray?,
    requirement: SignatureRequirement,
    isAutoFixed: Boolean,
    showBeforeAfter: Boolean,
    isProcessing: Boolean,
    onSignatureSelected: (android.net.Uri) -> Unit,
    onSampleClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onAutoFixClick: () -> Unit,
    onToggleBeforeAfter: () -> Unit,
    onDownloadClick: () -> Unit
) {
    val sigPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) onSignatureSelected(uri) }
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_signature_processing")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFDF4FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Draw,
                            contentDescription = null,
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "2. Candidate Signature",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DgNavy
                        )
                        Text(
                            text = "Required: ${requirement.displayDimensionString} • ${requirement.minKb}-${requirement.maxKb} KB",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                if (originalBitmap != null) {
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.testTag("btn_remove_signature")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove signature",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (originalBitmap == null) {
                // Upload Prompt Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFFCBD5E1),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upload Candidate Signature Scan/Photo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DgNavy
                        )
                        Text(
                            text = "Clean white paper with dark black/blue ink",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    sigPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DgNavy),
                                modifier = Modifier.testTag("btn_choose_signature")
                            ) {
                                Text(text = "Choose Signature", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onSampleClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA855F7)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA855F7)),
                                modifier = Modifier.testTag("btn_sample_signature")
                            ) {
                                Text(text = "Use Sample Signature", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                // Signature Loaded Preview
                val activeBitmap = if (showBeforeAfter) originalBitmap else (processedBitmap ?: originalBitmap)
                val targetAspect = requirement.width.toFloat() / requirement.height.toFloat()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Preview Frame
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .aspectRatio(targetAspect.coerceIn(1.2f, 3.5f))
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.5.dp, if (isAutoFixed) DgEmerald else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = activeBitmap.asImageBitmap(),
                            contentDescription = "Candidate Signature Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        if (showBeforeAfter) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(bottomEnd = 6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "ORIGINAL", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else if (isAutoFixed) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .background(DgEmerald.copy(alpha = 0.9f), RoundedCornerShape(bottomEnd = 6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "WHITENED", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Metadata & Actions
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val currentW = (processedBitmap ?: originalBitmap).width
                        val currentH = (processedBitmap ?: originalBitmap).height
                        val currentSizeKb = (processedBytes?.size ?: 0) / 1024f

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Dimensions:", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                "$currentW × $currentH px",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentW == requirement.width && currentH == requirement.height) DgEmerald else Color(0xFFEF4444)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("File Size:", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                if (currentSizeKb > 0) "${String.format("%.1f", currentSizeKb)} KB" else "Pending compression",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentSizeKb in (requirement.minKb.toFloat()..requirement.maxKb.toFloat())) DgEmerald else DgSaffron
                            )
                        }

                        // Auto-Fix Signature Button
                        Button(
                            onClick = onAutoFixClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isAutoFixed) DgEmerald else DgNavy),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_auto_fix_signature")
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Whitening...", fontSize = 12.sp)
                            } else {
                                Icon(
                                    imageVector = if (isAutoFixed) Icons.Default.CheckCircle else Icons.Default.AutoFixHigh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isAutoFixed) "Whitened to ${requirement.width}×${requirement.height} px" else "Auto-Whiten & Resize",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Action row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = onToggleBeforeAfter,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_toggle_sig_compare")
                            ) {
                                Icon(Icons.Default.Compare, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showBeforeAfter) "Show Fixed" else "Original",
                                    fontSize = 11.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onDownloadClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DgEmerald),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DgEmerald),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_download_signature")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Download", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            sigPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("btn_change_signature")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change Signature", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
