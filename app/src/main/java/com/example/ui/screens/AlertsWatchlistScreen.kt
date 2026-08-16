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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertEntity
import com.example.data.model.WatchlistEntity
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberTopAppBar
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
import com.example.ui.theme.StatusSecure
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
fun AlertsWatchlistScreen(viewModel: OsintViewModel) {
    val alerts by viewModel.alerts.collectAsState()
    val watchlists by viewModel.watchlists.collectAsState()
    val unreadCount by viewModel.unreadAlertCount.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Live Alerts, 1: Sentinel Watchlists
    var severityFilter by remember { mutableStateOf("ALL") }
    var showAddWatchlistDialog by remember { mutableStateOf(false) }

    val filteredAlerts = if (severityFilter == "ALL") alerts else alerts.filter { it.severity == severityFilter }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian)
    ) {
        CyberTopAppBar(
            title = "REAL-TIME ALERTS & WATCHLIST",
            subtitle = "CONTINUOUS THREAT & ANOMALY SURVEILLANCE",
            onLockClick = { viewModel.lockVault() },
            onPanicClick = { viewModel.panicWipeVault() }
        )

        // Sub-Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberDarkSurface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (activeTab == 0) CrimsonPrimary else CyberCardSurface)
                    .border(1.dp, if (activeTab == 0) CrimsonPrimary else CyberBorder, RoundedCornerShape(6.dp))
                    .clickable { activeTab = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LIVE SIGNALS (${alerts.size})",
                    color = if (activeTab == 0) Color.White else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (activeTab == 1) CrimsonPrimary else CyberCardSurface)
                    .border(1.dp, if (activeTab == 1) CrimsonPrimary else CyberBorder, RoundedCornerShape(6.dp))
                    .clickable { activeTab = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SENTINEL TARGETS (${watchlists.size})",
                    color = if (activeTab == 1) Color.White else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        if (activeTab == 0) {
            // Live Alerts Feed
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val filters = listOf("ALL", "CRITICAL", "HIGH", "MEDIUM", "LOW")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filters) { f ->
                                val isSelected = f == severityFilter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isSelected) CrimsonDark else CyberCardSurface)
                                        .border(1.dp, if (isSelected) CrimsonPrimary else CyberBorder, RoundedCornerShape(4.dp))
                                        .clickable { severityFilter = f }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = f,
                                        color = if (isSelected) CrimsonLight else TextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (unreadCount > 0) {
                            Text(
                                text = "MARK ALL READ",
                                color = CrimsonPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .clickable { viewModel.markAlertsRead() }
                                    .testTag("mark_alerts_read_button")
                            )
                        }
                    }
                }

                if (filteredAlerts.isEmpty()) {
                    item {
                        CyberCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No active alert signals in this severity threshold.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    items(filteredAlerts) { alert ->
                        AlertCardItem(
                            alert = alert,
                            onInvestigate = {
                                viewModel.generateAutomatedReportForTarget(
                                    targetName = alert.targetIdentifier,
                                    category = alert.category,
                                    rawData = "${alert.title}: ${alert.description}"
                                )
                            }
                        )
                    }
                }
            }
        } else {
            // Monitored Watchlists
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTINUOUS SENTINEL TARGETS",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        CyberButton(
                            text = "+ ADD TARGET",
                            onClick = { showAddWatchlistDialog = true },
                            isPrimary = true,
                            testTag = "add_watchlist_target_button"
                        )
                    }
                }

                items(watchlists) { target ->
                    WatchlistCardItem(
                        target = target,
                        onDelete = { viewModel.deleteWatchlist(target) },
                        onScanNow = {
                            viewModel.generateAutomatedReportForTarget(
                                targetName = target.targetQuery,
                                category = target.targetType,
                                rawData = "Periodic Sentinel Scan on ${target.targetQuery} (${target.tags})"
                            )
                        }
                    )
                }
            }
        }
    }

    if (showAddWatchlistDialog) {
        AddWatchlistDialog(
            onDismiss = { showAddWatchlistDialog = false },
            onAdd = { q, t, tags ->
                viewModel.addWatchlistTarget(q, t, tags)
                showAddWatchlistDialog = false
            }
        )
    }
}

@Composable
fun AlertCardItem(
    alert: AlertEntity,
    onInvestigate: () -> Unit
) {
    val dateStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(alert.timestamp))

    CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!alert.isRead) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CrimsonPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = alert.category.uppercase(),
                        color = CrimsonLight,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                ThreatBadge(severity = alert.severity)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${alert.source} ($dateStr)",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "AI INVESTIGATE ↗",
                    color = CrimsonPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable { onInvestigate() }
                )
            }
        }
    }
}

@Composable
fun WatchlistCardItem(
    target: WatchlistEntity,
    onDelete: () -> Unit,
    onScanNow: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(target.lastScanTimestamp))

    CyberCard(modifier = Modifier.fillMaxWidth()) {
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
                            .background(TerminalGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = target.targetType.uppercase(),
                        color = TerminalCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete target",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = target.targetQuery,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Tags: ${target.tags}",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last Scan: $dateStr (${target.alertMatches} Matches)",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "SCAN NOW ↗",
                    color = CrimsonPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable { onScanNow() }
                )
            }
        }
    }
}

@Composable
fun AddWatchlistDialog(
    onDismiss: () -> Unit,
    onAdd: (query: String, type: String, tags: String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Domain / Infrastructure") }
    var tags by remember { mutableStateOf("Continuous Scan, Critical") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberDarkSurface,
        title = {
            Text(
                text = "ADD SENTINEL TARGET",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Target Identifier (Domain, IP, Alias)", color = TextSecondary, fontSize = 11.sp) },
                    placeholder = { Text("e.g. corp-network.org", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
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

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Target Category", color = TextSecondary, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
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

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags & Scope", color = TextSecondary, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
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
            }
        },
        confirmButton = {
            CyberButton(
                text = "CONFIRM & MONITOR",
                onClick = {
                    if (query.isNotBlank()) onAdd(query, type, tags)
                },
                isPrimary = true
            )
        },
        dismissButton = {
            CyberButton(
                text = "CANCEL",
                onClick = onDismiss,
                isPrimary = false
            )
        }
    )
}
