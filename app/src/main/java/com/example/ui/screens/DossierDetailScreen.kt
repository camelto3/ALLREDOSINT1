package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DossierEntity
import com.example.ui.components.ClassificationBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.TelemetryDial
import com.example.ui.components.TerminalBlock
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusHigh
import com.example.ui.theme.StatusMedium
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.OsintViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DossierDetailScreen(
    dossier: DossierEntity,
    viewModel: OsintViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val decryptedPayload = viewModel.getDecryptedPayload(dossier)
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val speakingId by viewModel.currentSpeakingUtteranceId.collectAsState()
    val isCurrentSpeaking = isSpeaking && speakingId == "dossier_${dossier.id}"

    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(dossier.createdAt))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian)
    ) {
        // Detail Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberDarkSurface)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("dossier_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "ENCRYPTED DOSSIER",
                        color = CrimsonPrimary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CASE #${dossier.id}",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "=== SPECTRE OSINT DOSSIER ===\nTarget: ${dossier.target}\nClassification: ${dossier.classification}\n\nAI EXECUTIVE SUMMARY:\n${dossier.aiExecutiveSummary}\n\nDECRYPTED PAYLOAD:\n$decryptedPayload")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Export Intel Brief")
                        context.startActivity(shareIntent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Dossier",
                        tint = TextSecondary
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.deleteDossier(dossier)
                        onBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Dossier",
                        tint = StatusCritical
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClassificationBadge(level = dossier.classification)
                    Text(
                        text = dateStr,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            item {
                Text(
                    text = dossier.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Target: ${dossier.target} // Category: ${dossier.category}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Tags: ${dossier.tags}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            item {
                TelemetryDial(
                    label = "ASSESSED THREAT LEVEL",
                    valueText = "Threat Severity: ${dossier.threatScore}/100",
                    score = dossier.threatScore,
                    color = if (dossier.threatScore > 70) StatusCritical else StatusMedium
                )
            }

            // AI Executive Summary
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI EXECUTIVE BRIEF",
                                color = CrimsonPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )

                            // Audio Playback
                            CyberButton(
                                text = if (isCurrentSpeaking) "STOP VOICE" else "READ BRIEF",
                                onClick = {
                                    if (isCurrentSpeaking) {
                                        viewModel.stopSpeaking()
                                    } else {
                                        viewModel.speakAiMessage(
                                            com.example.data.model.AiAgentChatMessage(
                                                id = "dossier_${dossier.id}",
                                                sender = com.example.data.model.AiAgentChatMessage.AgentSender.AGENT,
                                                message = dossier.aiExecutiveSummary
                                            )
                                        )
                                    }
                                },
                                isPrimary = isCurrentSpeaking,
                                icon = if (isCurrentSpeaking) Icons.Default.GraphicEq else Icons.Default.VolumeUp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = dossier.aiExecutiveSummary,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Decrypted Raw Telemetry Block
            item {
                TerminalBlock(
                    title = "AES-256 GCM DECRYPTED RECON DATA",
                    content = decryptedPayload,
                    accentColor = TerminalGreen
                )
            }
        }
    }
}
