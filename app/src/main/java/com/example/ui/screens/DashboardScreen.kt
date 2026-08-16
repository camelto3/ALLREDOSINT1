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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.theme.StatusLow
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

    var omniSearchQuery by remember { mutableStateOf("") }

    val recentAlert = alerts.firstOrNull { !it.isRead } ?: alerts.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            CyberTopAppBar(
                title = "TACTICAL OSINT WORKBENCH",
                subtitle = "GLOBAL INTELLIGENCE AGGREGATOR // ACTIVE",
                onLockClick = { viewModel.lockVault() },
                onPanicClick = { viewModel.panicWipeVault() }
            )
        }

        // Omni Recon Search Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
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

        // Live Alert Ticker Banner
        if (recentAlert != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CrimsonDark.copy(alpha = 0.2f))
                        .border(1.dp, CrimsonPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { viewModel.setTab(AppNavTab.ALERTS) }
                        .padding(12.dp)
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
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = recentAlert.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                ThreatBadge(severity = recentAlert.severity)
                            }
                            Text(
                                text = recentAlert.description,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
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
        }

        // Telemetry Dials
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TelemetryDial(
                    label = "GLOBAL THREAT DEFCON RATING",
                    valueText = "ELEVATED POSTURE // LEVEL 2",
                    score = 78,
                    color = StatusHigh
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setTab(AppNavTab.ALERTS) }
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE ALERTS",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${alerts.size} Signals",
                                color = if (unreadAlertCount > 0) CrimsonPrimary else StatusSecure,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "$unreadAlertCount Unreviewed",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setTab(AppNavTab.VAULT) }
                    ) {
                        Column {
                            Text(
                                text = "ENCRYPTED DOSSIERS",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${dossiers.size} Files",
                                color = TerminalCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "AES-256 GCM Locked",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Tools Launchpad
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
                        text = "18 BUILT-IN ARSENAL TOOLS",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "EXPLORE ALL (18) ↗",
                        color = CrimsonPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable {
                            viewModel.setSelectedToolCategory(OsintToolCategory.ARSENAL)
                            viewModel.setTab(AppNavTab.TOOLS)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val tools = listOf(
                    Triple("CyberChef", Icons.Default.Build, OsintToolCategory.CYBERCHEF),
                    Triple("Sherlock", Icons.Default.PersonSearch, OsintToolCategory.FOOTPRINT),
                    Triple("PhoneInfoga", Icons.Default.Language, OsintToolCategory.PHONEINFOGA),
                    Triple("Shodan", Icons.Default.Language, OsintToolCategory.IP_NETWORK),
                    Triple("HIBP / DeHashed", Icons.Default.Email, OsintToolCategory.EMAIL_BREACH),
                    Triple("Nuclei / CVEs", Icons.Default.BugReport, OsintToolCategory.CVE_TRACKER),
                    Triple("PimEyes & EXIF", Icons.Default.PhotoCamera, OsintToolCategory.EXIF_FORENSICS),
                    Triple("DNS & WHOIS", Icons.Default.Dns, OsintToolCategory.DNS_DOMAIN)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(tools) { (name, icon, cat) ->
                        Box(
                            modifier = Modifier
                                .width(135.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCardSurface)
                                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setSelectedToolCategory(cat)
                                    viewModel.setTab(AppNavTab.TOOLS)
                                }
                                .padding(12.dp)
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
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = CrimsonLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = name,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Ready to use",
                                    color = TerminalGreen,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Dossiers Section
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "MANAGE VAULT",
                        color = CrimsonPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable { viewModel.setTab(AppNavTab.VAULT) }
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
                fontSize = 14.sp,
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
