package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertEntity
import com.example.data.model.DossierEntity
import com.example.ui.components.ClassificationBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberTopAppBar
import com.example.ui.components.TelemetryDial
import com.example.ui.components.ThreatBadge
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonLight
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusHigh
import com.example.ui.theme.StatusMedium
import com.example.ui.theme.StatusSecure
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.OsintToolCategory
import com.example.ui.viewmodel.OsintViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: OsintViewModel
) {
    val dossiers by viewModel.dossiers.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val unreadAlertCount by viewModel.unreadAlertCount.collectAsState()
    val watchlists by viewModel.watchlists.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val selfHealingLogs by viewModel.selfHealingLogs.collectAsState()
    val autoHealingActive by viewModel.isAutoHealingActive.collectAsState()
    val resolvedPatchesCount by viewModel.resolvedPatchesCount.collectAsState()

    var omniSearchQuery by remember { mutableStateOf("") }
    var quickAiPrompt by remember { mutableStateOf("") }

    val recentAlert = alerts.firstOrNull { !it.isRead } ?: alerts.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian)
            .testTag("main_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            CyberTopAppBar(
                title = "TACTICAL OSINT WORKBENCH",
                subtitle = "GLOBAL INTELLIGENCE AGGREGATOR // ACTIVE",
                onLockClick = { viewModel.lockVault() },
                onPanicClick = { viewModel.panicWipeVault() }
            )
        }

        // 1. QUICK-ACCESS GEMINI AI AGENT INPUT FIELD (HERO CARD)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    CyberCardElevated,
                                    CyberDarkSurface,
                                    CrimsonDark.copy(alpha = 0.25f)
                                )
                            )
                        )
                        .border(1.dp, CyberBorderGlow, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CrimsonPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = "Gemini AI Agent",
                                        tint = CrimsonLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "GEMINI AI INTELLIGENCE AGENT",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Ask threat actor profiling, IOC correlation, or CVE triage",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Text(
                                text = "OPEN CHAT ↗",
                                color = CrimsonPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .clickable { viewModel.setTab(AppNavTab.AI_ANALYST) }
                                    .testTag("open_full_ai_chat_button")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick input field
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = quickAiPrompt,
                                onValueChange = { quickAiPrompt = it },
                                placeholder = {
                                    Text(
                                        text = "Ask Gemini: 'Explain CVE-2026-3199' or 'Profile APT41'...",
                                        color = TextMuted,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = CrimsonPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (isAiThinking) {
                                        CircularProgressIndicator(
                                            color = CrimsonPrimary,
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dashboard_gemini_quick_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CyberObsidian,
                                    unfocusedContainerColor = CyberObsidian,
                                    focusedBorderColor = CrimsonPrimary,
                                    unfocusedBorderColor = CyberBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    val prompt = quickAiPrompt.trim()
                                    if (prompt.isNotBlank()) {
                                        viewModel.sendAiPrompt(prompt)
                                        quickAiPrompt = ""
                                        viewModel.setTab(AppNavTab.AI_ANALYST)
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CrimsonPrimary)
                                    .testTag("dashboard_gemini_submit_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send to Gemini",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Quick prompt suggestion pills
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(vertical = 2.dp)
                        ) {
                            val suggestions = listOf(
                                "Analyze APT29 TTPs",
                                "Correlate Origin IP",
                                "Dark Web Leak Impact",
                                "Explain CVE-2026-3199",
                                "Identify Phishing Indicators"
                            )
                            items(suggestions) { suggestion ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(CyberCardSurface)
                                        .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                                        .clickable {
                                            viewModel.sendAiPrompt(suggestion)
                                            viewModel.setTab(AppNavTab.AI_ANALYST)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = suggestion,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. UNIVERSAL RECON SCANNER (OMNI SEARCH)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "UNIVERSAL RECON SCANNER",
                    color = CrimsonPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = omniSearchQuery,
                        onValueChange = { omniSearchQuery = it },
                        placeholder = {
                            Text(
                                text = "Enter Domain, IP, Email, Handle or CVE...",
                                color = TextMuted,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = CrimsonPrimary
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("omni_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = CrimsonPrimary,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CyberButton(
                        text = "SCAN",
                        onClick = {
                            val q = omniSearchQuery.trim()
                            if (q.isNotBlank()) {
                                when {
                                    q.contains("@") -> {
                                        viewModel.setEmailQuery(q)
                                        viewModel.setSelectedToolCategory(OsintToolCategory.EMAIL_BREACH)
                                        viewModel.runEmailLookup(q)
                                    }
                                    q.matches(Regex("^(\\d{1,3}\\.){3}\\d{1,3}$")) -> {
                                        viewModel.setIpQuery(q)
                                        viewModel.setSelectedToolCategory(OsintToolCategory.IP_NETWORK)
                                        viewModel.runIpLookup(q)
                                    }
                                    q.startsWith("CVE-", ignoreCase = true) -> {
                                        viewModel.setCveSearchQuery(q)
                                        viewModel.setSelectedToolCategory(OsintToolCategory.CVE_TRACKER)
                                    }
                                    q.contains(".") -> {
                                        viewModel.setDnsQuery(q)
                                        viewModel.setSelectedToolCategory(OsintToolCategory.DNS_DOMAIN)
                                        viewModel.runDnsLookup(q)
                                    }
                                    else -> {
                                        viewModel.setFootprintQuery(q)
                                        viewModel.setSelectedToolCategory(OsintToolCategory.FOOTPRINT)
                                        viewModel.runFootprintScan(q)
                                    }
                                }
                                viewModel.setTab(AppNavTab.TOOLS)
                            }
                        },
                        testTag = "omni_scan_submit_button"
                    )
                }
            }
        }

        // 3. ACTIVE ALERT SUMMARIES & DEFCON TELEMETRY
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                // Header with review button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ACTIVE ALERT SUMMARIES",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        if (unreadAlertCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusCritical)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$unreadAlertCount NEW",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Text(
                        text = "VIEW ALL (${alerts.size}) ↗",
                        color = CrimsonPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { viewModel.setTab(AppNavTab.ALERTS) }
                            .testTag("dashboard_view_all_alerts")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Active Alert Highlight Banner
                if (recentAlert != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CrimsonDark.copy(alpha = 0.18f))
                            .border(1.dp, CrimsonPrimary.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .clickable { viewModel.setTab(AppNavTab.ALERTS) }
                            .padding(12.dp)
                            .testTag("dashboard_recent_alert_card")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = CrimsonPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = recentAlert.title,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    ThreatBadge(severity = recentAlert.severity)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = recentAlert.description,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Target: ${recentAlert.targetIdentifier}",
                                        color = TerminalCyan,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Source: ${recentAlert.source}",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "View Alerts",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Alert & Security Telemetry Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setTab(AppNavTab.ALERTS) }
                            .testTag("dashboard_alert_stat_card")
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE SIGNALS",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${alerts.size} Alerts",
                                color = if (unreadAlertCount > 0) CrimsonPrimary else StatusSecure,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "$unreadAlertCount Unreviewed",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setTab(AppNavTab.ALERTS) }
                            .testTag("dashboard_watchlist_stat_card")
                    ) {
                        Column {
                            Text(
                                text = "WATCHLIST TARGETS",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${watchlists.size} Monitored",
                                color = TerminalCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Continuous Sentinel",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setTab(AppNavTab.VAULT) }
                            .testTag("dashboard_vault_stat_card")
                    ) {
                        Column {
                            Text(
                                text = "ENCRYPTED VAULT",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${dossiers.size} Dossiers",
                                color = StatusSecure,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "AES-256 GCM",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AUTONOMIC SELF-HEALING & DIAGNOSTICS TELEMETRY
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCardElevated)
                        .border(1.dp, TerminalGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (autoHealingActive) TerminalGreen else StatusCritical)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SELF-HEALING AUTONOMIC ENGINE",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (autoHealingActive) "LEARNING / ACTIVE" else "STANDBY",
                                    color = if (autoHealingActive) TerminalGreen else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DIAGNOSTICS",
                                    color = CrimsonLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier
                                        .clickable { viewModel.triggerSelfHealingCheck() }
                                        .padding(2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Automated runtime interception & error mitigation. Intercepts OS driver warnings, memory pinning (ashmem), and network socket degradation to apply dynamic runtime bypass patches.",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        selfHealingLogs.firstOrNull()?.let { latestLog ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberDarkSurface)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "[AUTONOMOUS PATCH] ${latestLog.tag}",
                                            color = TerminalCyan,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "STATUS: RESOLVED",
                                            color = TerminalGreen,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = latestLog.patchApplied,
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. OSINT TOOLS NAVIGATION LAUNCHPAD (18 ARSENAL TOOLS)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OSINT TOOLS NAVIGATION",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "ARSENAL (18) ↗",
                        color = CrimsonPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable {
                                viewModel.setSelectedToolCategory(OsintToolCategory.ARSENAL)
                                viewModel.setTab(AppNavTab.TOOLS)
                            }
                            .testTag("dashboard_explore_all_tools")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val quickTools = listOf(
                    ToolNavItem("CyberChef", "Transforms & Defang", Icons.Default.Build, OsintToolCategory.CYBERCHEF, CrimsonLight),
                    ToolNavItem("Sherlock", "Social Footprints", Icons.Default.PersonSearch, OsintToolCategory.FOOTPRINT, TerminalCyan),
                    ToolNavItem("IP & Shodan", "Geo & ASN Intel", Icons.Default.Language, OsintToolCategory.IP_NETWORK, TerminalGreen),
                    ToolNavItem("DNS & WHOIS", "DoH & Subdomains", Icons.Default.Dns, OsintToolCategory.DNS_DOMAIN, CrimsonLight),
                    ToolNavItem("PhoneInfoga", "Carrier & VoIP", Icons.Default.Language, OsintToolCategory.PHONEINFOGA, TerminalCyan),
                    ToolNavItem("HIBP Breach", "Leaked Credentials", Icons.Default.Email, OsintToolCategory.EMAIL_BREACH, StatusCritical),
                    ToolNavItem("CVE Tracker", "Vulnerability Intel", Icons.Default.BugReport, OsintToolCategory.CVE_TRACKER, StatusHigh),
                    ToolNavItem("EXIF Forensics", "Metadata & Geotags", Icons.Default.PhotoCamera, OsintToolCategory.EXIF_FORENSICS, TerminalGreen)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(quickTools) { item ->
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCardSurface)
                                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setSelectedToolCategory(item.category)
                                    viewModel.setTab(AppNavTab.TOOLS)
                                }
                                .padding(12.dp)
                                .testTag("dashboard_tool_nav_${item.category.name.lowercase()}")
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberBorderGlow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.name,
                                        tint = item.tint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.name,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = item.subtitle,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "LAUNCH ↗",
                                    color = CrimsonPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. RECENT ENCRYPTED CASE DOSSIERS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ENCRYPTED CASE DOSSIERS",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "MANAGE VAULT ↗",
                        color = CrimsonPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { viewModel.setTab(AppNavTab.VAULT) }
                            .testTag("dashboard_manage_vault_button")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (dossiers.isEmpty()) {
                    CyberCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No saved dossiers. Perform a scan and click 'Generate AI Dossier' to persist encrypted investigations.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        dossiers.take(3).forEach { dossier ->
                            DossierRowItem(
                                dossier = dossier,
                                onClick = {
                                    viewModel.selectDossier(dossier)
                                    viewModel.setTab(AppNavTab.VAULT)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ToolNavItem(
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val category: OsintToolCategory,
    val tint: Color
)

@Composable
fun DossierRowItem(
    dossier: DossierEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(dossier.createdAt))

    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClassificationBadge(level = dossier.classification)
                Text(
                    text = dateStr,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = dossier.title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Target: ${dossier.target} // Category: ${dossier.category}",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Risk Score: ${dossier.threatScore}/100",
                    color = if (dossier.threatScore > 70) StatusCritical else StatusMedium,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = TerminalCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AES-256 E2EE",
                        color = TerminalCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
