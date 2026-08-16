package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiAgentChatMessage
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberTopAppBar
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonLight
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.OsintViewModel

@Composable
fun AiAnalystScreen(viewModel: OsintViewModel) {
    val messages by viewModel.aiMessages.collectAsState()
    val inputText by viewModel.aiInputText.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val speakingUtteranceId by viewModel.currentSpeakingUtteranceId.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "Analyze Threat Actor TTPs",
        "Correlate DNS & Origin IP",
        "Credential Leak Impact Assessment",
        "Generate Attack Surface Matrix",
        "Assess Zero-Day Exploitability"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian)
    ) {
        CyberTopAppBar(
            title = "AEGIS-PRIME AI ANALYST",
            subtitle = "ENGLISH INTELLIGENCE SYNTHESIS & VOICE AGENT",
            onLockClick = { viewModel.lockVault() },
            onPanicClick = { viewModel.panicWipeVault() }
        )

        // Chat Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(
                    message = message,
                    isSpeaking = isSpeaking && speakingUtteranceId == message.id,
                    onSpeakToggle = { viewModel.speakAiMessage(message) },
                    onSaveToVault = {
                        viewModel.generateAutomatedReportForTarget(
                            targetName = "AI Brief #${message.id.take(6)}",
                            category = "AI Synthesis",
                            rawData = message.message
                        )
                    }
                )
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = CrimsonPrimary,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AEGIS-PRIME is correlating intelligence indicators...",
                            color = CrimsonLight,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Quick Prompt Presets
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberDarkSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberCardElevated)
                        .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                        .clickable { viewModel.sendAiPrompt(prompt) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = prompt,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Prompt Input Field & Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberDarkSurface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { viewModel.setAiInputText(it) },
                placeholder = {
                    Text(
                        text = "Instruct AEGIS-PRIME on targets or threats...",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberObsidian,
                    unfocusedContainerColor = CyberObsidian,
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { viewModel.sendAiPrompt() },
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CrimsonPrimary)
                    .testTag("send_ai_prompt_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Prompt",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: AiAgentChatMessage,
    isSpeaking: Boolean,
    onSpeakToggle: () -> Unit,
    onSaveToVault: () -> Unit
) {
    val isUser = message.sender == AiAgentChatMessage.AgentSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CrimsonPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = if (isUser) "ANALYST" else "AEGIS-PRIME AGENT // ENGLISH AUDIO",
                color = if (isUser) TextSecondary else CrimsonPrimary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.85f else 1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isUser) CyberCardElevated else CyberCardSurface)
                .border(
                    1.dp,
                    if (isUser) CyberBorder else CyberBorderGlow,
                    RoundedCornerShape(10.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = message.message,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontFamily = if (isUser) FontFamily.Default else FontFamily.Monospace,
                    lineHeight = 18.sp
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(10.dp))

                    if (message.citations.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "GROUNDING: ",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = message.citations.joinToString(", "),
                                color = TerminalCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Voice Synthesizer Button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSpeaking) CrimsonPrimary else CyberObsidian)
                                .border(1.dp, CrimsonPrimary, RoundedCornerShape(4.dp))
                                .clickable { onSpeakToggle() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                                contentDescription = "Voice Readout",
                                tint = if (isSpeaking) Color.White else CrimsonPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSpeaking) "SPEAKING..." else "PLAY AUDIO",
                                color = if (isSpeaking) Color.White else CrimsonPrimary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Save to Vault Button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberObsidian)
                                .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
                                .clickable { onSaveToVault() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Encrypt to Vault",
                                tint = TerminalCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SAVE DOSSIER",
                                color = TerminalCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
