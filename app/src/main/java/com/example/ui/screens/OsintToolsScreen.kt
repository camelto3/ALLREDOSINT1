package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArsenalTool
import com.example.data.model.CveItem
import com.example.data.model.GoogleDorkItem
import com.example.data.model.ToolCategory
import com.example.service.CyberChefOp
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberTopAppBar
import com.example.ui.components.TelemetryDial
import com.example.ui.components.TerminalBlock
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
import com.example.ui.viewmodel.OsintToolCategory
import com.example.ui.viewmodel.OsintViewModel

@Composable
fun OsintToolsScreen(viewModel: OsintViewModel) {
    val selectedCategory by viewModel.selectedToolCategory.collectAsState()
    val selectedArsenalTool by viewModel.selectedArsenalTool.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian)
    ) {
        CyberTopAppBar(
            title = "OSINT & RECON ARSENAL",
            subtitle = "18 BUILT-IN SECURITY & THREAT INTELLIGENCE TOOLS",
            onLockClick = { viewModel.lockVault() },
            onPanicClick = { viewModel.panicWipeVault() }
        )

        // Tool Selection Tabs
        val categories = OsintToolCategory.values()
        ScrollableTabRow(
            selectedTabIndex = selectedCategory.ordinal,
            containerColor = CyberDarkSurface,
            contentColor = CrimsonPrimary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedCategory.ordinal]),
                    color = CrimsonPrimary,
                    height = 2.dp
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CyberBorder)
                )
            }
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedToolCategory(category) },
                    modifier = Modifier.testTag("tool_tab_${category.name.lowercase()}"),
                    text = {
                        Text(
                            text = category.title.uppercase(),
                            color = if (isSelected) CrimsonPrimary else TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Active Tool Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedCategory) {
                OsintToolCategory.ARSENAL -> ArsenalCatalogView(viewModel)
                OsintToolCategory.CYBERCHEF -> CyberChefToolView(viewModel)
                OsintToolCategory.PHONEINFOGA -> PhoneInfogaToolView(viewModel)
                OsintToolCategory.DNS_DOMAIN -> DnsDomainToolView(viewModel)
                OsintToolCategory.IP_NETWORK -> IpNetworkToolView(viewModel)
                OsintToolCategory.FOOTPRINT -> FootprintToolView(viewModel)
                OsintToolCategory.EMAIL_BREACH -> EmailBreachToolView(viewModel)
                OsintToolCategory.CVE_TRACKER -> CveTrackerToolView(viewModel)
                OsintToolCategory.GOOGLE_DORKS -> GoogleDorksToolView(viewModel)
                OsintToolCategory.EXIF_FORENSICS -> ExifForensicsToolView(viewModel)
            }
        }
    }

    if (selectedArsenalTool != null) {
        ArsenalToolDetailDialog(
            tool = selectedArsenalTool!!,
            onDismiss = { viewModel.selectArsenalTool(null) },
            onLaunchRunner = {
                when (selectedArsenalTool!!.id) {
                    "cyberchef" -> viewModel.setSelectedToolCategory(OsintToolCategory.CYBERCHEF)
                    "phoneinfoga" -> viewModel.setSelectedToolCategory(OsintToolCategory.PHONEINFOGA)
                    "sherlock", "social_analyzer" -> viewModel.setSelectedToolCategory(OsintToolCategory.FOOTPRINT)
                    "hibp", "dehashed", "epieos" -> viewModel.setSelectedToolCategory(OsintToolCategory.EMAIL_BREACH)
                    "shodan", "spiderfoot", "recon_ng" -> viewModel.setSelectedToolCategory(OsintToolCategory.IP_NETWORK)
                    "nuclei", "caido", "evilginx3" -> viewModel.setSelectedToolCategory(OsintToolCategory.CVE_TRACKER)
                    "pimeyes" -> viewModel.setSelectedToolCategory(OsintToolCategory.EXIF_FORENSICS)
                    else -> viewModel.launchExternalUrl(context, selectedArsenalTool!!.officialUrl)
                }
                viewModel.selectArsenalTool(null)
            },
            onOpenUrl = { url -> viewModel.launchExternalUrl(context, url) }
        )
    }
}

// -------------------------------------------------------------
// 0. ARSENAL CATALOG VIEW (ALL 18 TOOLS)
// -------------------------------------------------------------
@Composable
fun ArsenalCatalogView(viewModel: OsintViewModel) {
    val tools = viewModel.arsenalTools
    val selectedCategoryFilter by viewModel.selectedArsenalCategory.collectAsState()
    val searchQuery by viewModel.arsenalSearchQuery.collectAsState()

    val filteredTools = tools.filter { tool ->
        val matchesCategory = selectedCategoryFilter == null || tool.category == selectedCategoryFilter
        val matchesSearch = searchQuery.isBlank() ||
                tool.name.contains(searchQuery, ignoreCase = true) ||
                tool.description.contains(searchQuery, ignoreCase = true) ||
                tool.keyFeatures.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setArsenalSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search 18 Built-in OSINT Tools (Sherlock, Shodan, HIBP, Nuclei...)",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CrimsonPrimary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("arsenal_search_input"),
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
        }

        // Category Filter Chips
        item {
            val categories = listOf(null) + ToolCategory.values()
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategoryFilter
                    val label = cat?.title ?: "ALL 18 TOOLS"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) CrimsonPrimary else CyberCardSurface)
                            .border(1.dp, if (isSelected) CrimsonPrimary else CyberBorder, RoundedCornerShape(6.dp))
                            .clickable { viewModel.setArsenalCategoryFilter(cat) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label.uppercase(),
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        items(filteredTools) { tool ->
            ArsenalToolCard(
                tool = tool,
                onClick = { viewModel.selectArsenalTool(tool) }
            )
        }
    }
}

@Composable
fun ArsenalToolCard(
    tool: ArsenalTool,
    onClick: () -> Unit
) {
    val icon = when (tool.iconType) {
        "person_search" -> Icons.Default.PersonSearch
        "search" -> Icons.Default.Search
        "photo_camera" -> Icons.Default.PhotoCamera
        "language" -> Icons.Default.Language
        "hub" -> Icons.Default.Hub
        "mark_email_unread" -> Icons.Default.MarkEmailUnread
        "password" -> Icons.Default.Password
        "router" -> Icons.Default.Router
        "phone_iphone" -> Icons.Default.PhoneIphone
        "account_circle" -> Icons.Default.AccountCircle
        "shield" -> Icons.Default.Shield
        "cloud" -> Icons.Default.Cloud
        "radar" -> Icons.Default.Radar
        "bug_report" -> Icons.Default.BugReport
        "flash_on" -> Icons.Default.FlashOn
        "build" -> Icons.Default.Build
        "account_tree" -> Icons.Default.AccountTree
        "terminal" -> Icons.Default.Terminal
        else -> Icons.Default.Build
    }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CrimsonDark.copy(alpha = 0.3f))
                            .border(1.dp, CrimsonPrimary.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tool.name,
                            tint = CrimsonLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = tool.name,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = tool.category.title.uppercase(),
                            color = TerminalCyan,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberObsidian)
                        .border(1.dp, CyberBorderGlow, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "READY",
                        color = TerminalGreen,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tool.tagline,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VIEW MANUAL & RUNNER ↗",
                    color = CrimsonPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "BUILT-IN",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ArsenalToolDetailDialog(
    tool: ArsenalTool,
    onDismiss: () -> Unit,
    onLaunchRunner: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberDarkSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tool.name.uppercase(),
                        color = CrimsonPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = tool.category.title.uppercase(),
                        color = TerminalCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "DESCRIPTION",
                        color = CrimsonPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tool.description,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }

                item {
                    Text(
                        text = "HOW TO USE (TACTICAL WORKFLOW)",
                        color = TerminalGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tool.howToUse,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 17.sp
                    )
                }

                item {
                    Text(
                        text = "PRIMARY SYNTAX / COMMAND",
                        color = TerminalCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberObsidian)
                            .border(1.dp, CyberBorderGlow, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tool.primarySyntax,
                                color = TerminalGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(tool.primarySyntax))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy syntax",
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "KEY CAPABILITIES",
                        color = CrimsonLight,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    tool.keyFeatures.forEach { feat ->
                        Text(
                            text = "• $feat",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                item {
                    Text(
                        text = "DEFENSIVE / MITIGATION NOTE",
                        color = StatusCritical,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tool.riskOrDefensiveNote,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        },
        confirmButton = {
            CyberButton(
                text = "LAUNCH IN-APP RUNNER",
                onClick = onLaunchRunner,
                icon = Icons.Default.PlayArrow,
                isPrimary = true
            )
        },
        dismissButton = {
            CyberButton(
                text = "OFFICIAL DOCS ↗",
                onClick = { onOpenUrl(tool.officialUrl) },
                icon = Icons.Default.OpenInBrowser,
                isPrimary = false
            )
        }
    )
}

// -------------------------------------------------------------
// 0.1 CYBERCHEF INTERACTIVE SUITE VIEW
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberChefToolView(viewModel: OsintViewModel) {
    val input by viewModel.cyberChefInput.collectAsState()
    val selectedOp by viewModel.cyberChefSelectedOp.collectAsState()
    val output by viewModel.cyberChefOutput.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var expandedDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "CYBERCHEF FORENSIC ENGINE // RECIPE SUITE",
                color = CrimsonPrimary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Client-side cryptographic transforms, IOC defanging, token decoders, and regex parsers.",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        // Operation Picker Dropdown
        item {
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown }
            ) {
                OutlinedTextField(
                    value = selectedOp.title,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Active Recipe Operation", color = TextSecondary, fontSize = 11.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberDarkSurface,
                        unfocusedContainerColor = CyberDarkSurface,
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("cyberchef_op_dropdown")
                )

                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false },
                    modifier = Modifier.background(CyberDarkSurface)
                ) {
                    CyberChefOp.values().forEach { op ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = op.title,
                                    color = if (op == selectedOp) CrimsonPrimary else TextPrimary,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            onClick = {
                                viewModel.setCyberChefOp(op)
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }
        }

        // Input Field
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INPUT TEXT / PAYLOAD",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Row {
                        Text(
                            text = "SAMPLE",
                            color = TerminalCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clickable {
                                    viewModel.setCyberChefInput("hxxps://malicious-c2[.]internal/drop/payload?beacon=true")
                                }
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CLEAR",
                            color = CrimsonLight,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clickable { viewModel.setCyberChefInput("") }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { viewModel.setCyberChefInput(it) },
                    placeholder = { Text("Paste string, base64 payload, JWT, or obfuscated URL...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cyberchef_input_box"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberDarkSurface,
                        unfocusedContainerColor = CyberDarkSurface,
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 3,
                    maxLines = 6
                )
            }
        }

        // Output Terminal
        item {
            TerminalBlock(
                title = "CYBERCHEF OUTPUT [${selectedOp.title.uppercase()}]",
                content = if (output.isNotBlank()) output else "[Awaiting Input Payload]",
                accentColor = TerminalGreen
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CyberButton(
                    text = "COPY RESULT",
                    onClick = {
                        clipboardManager.setText(AnnotatedString(output))
                        viewModel.showToast("Output copied to clipboard")
                    },
                    icon = Icons.Default.ContentCopy,
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )

                CyberButton(
                    text = "ANALYZE WITH AI",
                    onClick = {
                        viewModel.sendAiPrompt("Analyze this deobfuscated CyberChef result and identify any threats, IoCs, or decoding patterns:\n$output")
                        viewModel.setTab(com.example.ui.viewmodel.AppNavTab.AI_ANALYST)
                    },
                    icon = Icons.Default.Psychology,
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 0.2 PHONEINFOGA INTERACTIVE TOOL VIEW
// -------------------------------------------------------------
@Composable
fun PhoneInfogaToolView(viewModel: OsintViewModel) {
    val query by viewModel.phoneQuery.collectAsState()
    val result by viewModel.phoneResult.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SearchInputSection(
                label = "PHONEINFOGA // INTERNATIONAL NUMBER RECON",
                placeholder = "+14155552671 or +447911123456",
                query = query,
                onQueryChange = { viewModel.setPhoneQuery(it) },
                onScan = { viewModel.runPhoneLookup(query) },
                isLoading = false,
                testTag = "phoneinfoga_scan_button"
            )
        }

        if (result != null) {
            val res = result!!
            item {
                TelemetryDial(
                    label = "LINE CLASSIFICATION",
                    valueText = "${res.countryName} (${res.lineType})",
                    score = if (res.lineType.contains("VoIP", ignoreCase = true)) 65 else 15,
                    color = if (res.lineType.contains("VoIP", ignoreCase = true)) StatusHigh else StatusSecure
                )
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "NUMBER TELEMETRY & CARRIER ESTIMATE",
                            color = CrimsonPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        DataRow("E.164 Standard Format", res.formattedE164)
                        DataRow("Country / Calling Code", "${res.countryName} (${res.countryCode})")
                        DataRow("Carrier Estimate", res.carrierEstimate)
                        DataRow("Estimated Line Type", res.lineType)
                        DataRow("Approximate Timezone", res.timeZone)
                        DataRow("VoIP / Burner Risk", res.riskRating)
                    }
                }
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "AUTOMATED OSINT SEARCH DORKS",
                            color = TerminalCyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        res.searchDorks.forEach { dork ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (dork.startsWith("http")) {
                                            viewModel.launchExternalUrl(context, dork)
                                        } else {
                                            viewModel.launchExternalUrl(context, "https://www.google.com/search?q=" + Uri.encode(dork))
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• $dork",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "OPEN ↗",
                                    color = CrimsonPrimary,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                CyberButton(
                    text = "SAVE PHONE INTEL TO VAULT",
                    onClick = {
                        val rawData = "Phone: ${res.formattedE164}, Country: ${res.countryName}, Carrier: ${res.carrierEstimate}, LineType: ${res.lineType}"
                        viewModel.generateAutomatedReportForTarget(res.formattedE164, "Phone Recon", rawData)
                    },
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. DNS & DOMAIN TOOL VIEW
// -------------------------------------------------------------
@Composable
fun DnsDomainToolView(viewModel: OsintViewModel) {
    val query by viewModel.dnsQuery.collectAsState()
    val result by viewModel.dnsResult.collectAsState()
    val isLoading by viewModel.isDnsLoading.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SearchInputSection(
                label = "TARGET DOMAIN // HOSTNAME",
                placeholder = "e.g. google.com, proton.me, apex-sec.io",
                query = query,
                onQueryChange = { viewModel.setDnsQuery(it) },
                onScan = { viewModel.runDnsLookup(query) },
                isLoading = isLoading,
                testTag = "dns_scan_button"
            )
        }

        if (result != null) {
            val res = result!!
            item {
                TelemetryDial(
                    label = "DOMAIN EXPOSURE RATING",
                    valueText = "${res.domain} (Risk: ${res.riskScore}/100)",
                    score = res.riskScore,
                    color = if (res.riskScore > 50) StatusHigh else StatusSecure
                )
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "REGISTRY & WHOIS TELEMETRY",
                            color = CrimsonPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        DataRow("Registrar", res.registrar)
                        DataRow("Created On", res.createdDate)
                        DataRow("Expires On", res.expiresDate)
                        DataRow("TLS Issuer", res.sslIssuer)
                        if (res.cname != null) {
                            DataRow("Canonical Name (CNAME)", res.cname ?: "")
                        }
                    }
                }
            }

            item {
                TerminalBlock(
                    title = "DNS RECORDS // RESOLUTION MATRIX",
                    content = buildString {
                        appendLine("=== A / AAAA IP NODES ===")
                        res.ipAddresses.forEach { appendLine("  - $it") }
                        appendLine("\n=== MAIL EXCHANGERS (MX) ===")
                        res.mxRecords.forEach { appendLine("  - $it") }
                        appendLine("\n=== NAMESERVERS (NS) ===")
                        res.nsRecords.forEach { appendLine("  - $it") }
                        appendLine("\n=== TXT / SPF / VERIFICATION ===")
                        res.txtRecords.forEach { appendLine("  - $it") }
                    }
                )
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "DISCOVERED SUBDOMAINS",
                            color = TerminalCyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        res.subdomainsDiscovered.forEach { sub ->
                            Text(
                                text = "• $sub",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            item {
                CyberButton(
                    text = "GENERATE AI DOSSIER REPORT",
                    onClick = {
                        val rawData = "Domain: ${res.domain}, IPs: ${res.ipAddresses.joinToString()}, MX: ${res.mxRecords.joinToString()}, Registrar: ${res.registrar}"
                        viewModel.generateAutomatedReportForTarget(res.domain, "Domain Recon", rawData)
                    },
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "generate_ai_dossier_dns"
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. IP & NETWORK TOOL VIEW (SHODAN / SPIDERFOOT)
// -------------------------------------------------------------
@Composable
fun IpNetworkToolView(viewModel: OsintViewModel) {
    val query by viewModel.ipQuery.collectAsState()
    val result by viewModel.ipResult.collectAsState()
    val isLoading by viewModel.isIpLoading.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SearchInputSection(
                label = "SHODAN & IP NETWORK SCANNER",
                placeholder = "e.g. 1.1.1.1, 8.8.8.8, 104.21.45.188",
                query = query,
                onQueryChange = { viewModel.setIpQuery(it) },
                onScan = { viewModel.runIpLookup(query) },
                isLoading = isLoading,
                testTag = "ip_scan_button"
            )
        }

        if (result != null) {
            val res = result!!
            item {
                TelemetryDial(
                    label = "SHODAN ABUSE CONFIDENCE SCORE",
                    valueText = "${res.ip} (${res.abuseConfidenceScore}% Abuse Probability)",
                    score = res.abuseConfidenceScore,
                    color = if (res.abuseConfidenceScore > 50) StatusCritical else StatusSecure
                )
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "NETWORK GEOLOCATION & BGP INFO",
                            color = CrimsonPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        DataRow("Autonomous System (ASN)", res.asn)
                        DataRow("ISP / Carrier", res.isp)
                        DataRow("Organization", res.org)
                        DataRow("Location", "${res.city}, ${res.region}, ${res.country} (${res.countryCode})")
                        DataRow("Coordinates", "Lat ${res.latitude}, Lon ${res.longitude}")
                        DataRow("Reverse DNS (PTR)", res.hostname)
                        DataRow("Tor Exit Node", if (res.isTorNode) "YES (DETECTED)" else "NO")
                        DataRow("VPN / Proxy", if (res.isVpnProxy) "YES (PROBABLE)" else "NO")
                    }
                }
            }

            item {
                TerminalBlock(
                    title = "OPEN PORTS & THREAT VECTOR ASSESSMENT",
                    content = buildString {
                        appendLine("Detected Active Ingress Ports:")
                        res.openPortsDetected.forEach { p ->
                            val desc = when (p) {
                                80 -> "HTTP (Web Ingress)"
                                443 -> "HTTPS (TLS Ingress)"
                                8080 -> "HTTP-Alt / Proxy"
                                8443 -> "HTTPS-Alt / Admin"
                                else -> "Custom Protocol"
                            }
                            appendLine("  [OPEN] Port $p/tcp - $desc")
                        }
                        appendLine("\nAssessment Summary:")
                        appendLine(res.threatSummary)
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberButton(
                        text = "SHODAN HOST ↗",
                        onClick = {
                            viewModel.launchExternalUrl(context, "https://www.shodan.io/host/${res.ip}")
                        },
                        isPrimary = false,
                        icon = Icons.Default.Launch,
                        modifier = Modifier.weight(1f)
                    )

                    CyberButton(
                        text = "SAVE DOSSIER",
                        onClick = {
                            val payload = "IP: ${res.ip}, ASN: ${res.asn}, ISP: ${res.isp}, Geo: ${res.city}, ${res.country}, AbuseScore: ${res.abuseConfidenceScore}"
                            viewModel.generateAutomatedReportForTarget(res.ip, "IP Infrastructure", payload)
                        },
                        isPrimary = true,
                        icon = Icons.Default.Psychology,
                        modifier = Modifier.weight(1f),
                        testTag = "generate_ai_dossier_ip"
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. DIGITAL FOOTPRINT (SHERLOCK & SOCIAL ANALYZER)
// -------------------------------------------------------------
@Composable
fun FootprintToolView(viewModel: OsintViewModel) {
    val query by viewModel.footprintQuery.collectAsState()
    val hits by viewModel.footprintResults.collectAsState()
    val isLoading by viewModel.isFootprintLoading.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SearchInputSection(
                label = "SHERLOCK & SOCIAL ANALYZER // ALIAS RECON",
                placeholder = "e.g. torvalds, octocat, john_doe",
                query = query,
                onQueryChange = { viewModel.setFootprintQuery(it) },
                onScan = { viewModel.runFootprintScan(query) },
                isLoading = isLoading,
                testTag = "footprint_scan_button"
            )
        }

        if (hits.isNotEmpty()) {
            item {
                val confirmedCount = hits.count { it.exists }
                TelemetryDial(
                    label = "SHERLOCK PLATFORM DENSITY",
                    valueText = "$confirmedCount of ${hits.size} Platforms Matched",
                    score = ((confirmedCount.toFloat() / hits.size) * 100).toInt(),
                    color = if (confirmedCount > 5) StatusHigh else TerminalCyan
                )
            }

            items(hits) { hit ->
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(hit.profileUrl))
                        context.startActivity(intent)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = hit.platformName,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(CyberBorderGlow)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = hit.category,
                                        color = TextSecondary,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Text(
                                text = hit.profileUrl,
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ThreatBadge(severity = if (hit.exists) "HIGH" else "LOW")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Open in browser",
                                tint = CrimsonLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            item {
                CyberButton(
                    text = "CORRELATE ALIAS WITH AI",
                    onClick = {
                        val summary = "Username: $query, Matched Platforms: " + hits.filter { it.exists }.joinToString { it.platformName }
                        viewModel.generateAutomatedReportForTarget(query, "Persona Recon", summary)
                    },
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. EMAIL & BREACH TOOL (HIBP, DEHASHED, EPIEOS)
// -------------------------------------------------------------
@Composable
fun EmailBreachToolView(viewModel: OsintViewModel) {
    val query by viewModel.emailQuery.collectAsState()
    val result by viewModel.emailResult.collectAsState()
    val isLoading by viewModel.isEmailLoading.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SearchInputSection(
                label = "HIBP, DEHASHED & EPIEOS // EMAIL INTEL",
                placeholder = "e.g. analyst@company.com",
                query = query,
                onQueryChange = { viewModel.setEmailQuery(it) },
                onScan = { viewModel.runEmailLookup(query) },
                isLoading = isLoading,
                testTag = "email_scan_button"
            )
        }

        if (result != null) {
            val res = result!!
            item {
                TelemetryDial(
                    label = "HIBP BREACH RISK EXPOSURE",
                    valueText = "${res.breachCount} Correlated Leaks (${res.riskLevel})",
                    score = (res.breachCount * 25).coerceIn(0, 100),
                    color = if (res.breachCount > 0) StatusCritical else StatusSecure
                )
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "EMAIL SYNTAX & EPIEOS IDENTITY CORRELATION",
                            color = CrimsonPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        DataRow("Username Part", res.userPart)
                        DataRow("Domain Part", res.domainPart)
                        DataRow("Syntax Valid", if (res.isValidFormat) "VALID RFC-5322" else "INVALID")
                        DataRow("MX Host Active", if (res.hasValidMx) "ACTIVE ROUTING" else "NO MX FOUND")
                        DataRow("Disposable Email", if (res.isDisposable) "YES (BURNER)" else "NO (LEGITIMATE)")
                    }
                }
            }

            item {
                TerminalBlock(
                    title = "DEHASHED & HIBP BREACH CORRELATIONS",
                    content = if (res.knownBreaches.isNotEmpty()) {
                        buildString {
                            appendLine("Identified in Public Combo Lists / Leaks:")
                            res.knownBreaches.forEachIndexed { i, b ->
                                appendLine("  [${i + 1}] $b")
                            }
                            appendLine("\nRecommendation: Check DeHashed for password hash reuse; enforce FIDO2 tokens.")
                        }
                    } else {
                        "No direct breach records matched in global credential leaks."
                    },
                    accentColor = if (res.knownBreaches.isNotEmpty()) StatusCritical else TerminalGreen
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberButton(
                        text = "EPIEOS REVERSE ↗",
                        onClick = {
                            viewModel.launchExternalUrl(context, "https://epieos.com/?q=${res.email}&t=email")
                        },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )

                    CyberButton(
                        text = "SAVE DOSSIER",
                        onClick = {
                            val data = "Email: ${res.email}, Breaches: ${res.knownBreaches.joinToString()}, Risk: ${res.riskLevel}"
                            viewModel.generateAutomatedReportForTarget(res.email, "Email Intel", data)
                        },
                        isPrimary = true,
                        icon = Icons.Default.Psychology,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. CVE TRACKER (NUCLEI TEMPLATES & ADVISORIES)
// -------------------------------------------------------------
@Composable
fun CveTrackerToolView(viewModel: OsintViewModel) {
    val searchQuery by viewModel.cveSearchQuery.collectAsState()
    val cveList by viewModel.cveList.collectAsState()
    val context = LocalContext.current

    val filtered = if (searchQuery.isBlank()) cveList else {
        cveList.filter {
            it.cveId.contains(searchQuery, ignoreCase = true) ||
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.affectedProducts.any { p -> p.contains(searchQuery, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setCveSearchQuery(it) },
                placeholder = { Text("Search Nuclei Templates & CVE Advisories...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.BugReport, contentDescription = null, tint = CrimsonPrimary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cve_search_input"),
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
        }

        items(filtered) { cve ->
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cve.referenceUrl))
                    context.startActivity(intent)
                }
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cve.cveId,
                            color = CrimsonPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        ThreatBadge(severity = cve.severity.name)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cve.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = cve.summary,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CVSS: ${cve.cvssScore}",
                            color = if (cve.cvssScore >= 9.0) StatusCritical else StatusHigh,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Nuclei Template Verified ↗",
                            color = TerminalGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. GOOGLE DORKS VIEW
// -------------------------------------------------------------
@Composable
fun GoogleDorksToolView(viewModel: OsintViewModel) {
    val dorks by viewModel.googleDorks.collectAsState()
    val activeCategory by viewModel.dorkCategoryFilter.collectAsState()
    val context = LocalContext.current

    var targetDomainInput by remember { mutableStateOf("") }

    val categories = listOf("All", "Credentials & Auth", "Exposed Services", "Cloud & Storage", "IoT & Hardware", "Logs & Diagnostics")
    val filtered = if (activeCategory == "All") dorks else dorks.filter { it.category == activeCategory }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = targetDomainInput,
                onValueChange = { targetDomainInput = it },
                label = { Text("Scope Target Domain (Optional)", color = TextSecondary, fontSize = 11.sp) },
                placeholder = { Text("e.g. example.com", color = TextMuted, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
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
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    val isSelected = cat == activeCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) CrimsonPrimary else CyberCardSurface)
                            .border(1.dp, if (isSelected) CrimsonPrimary else CyberBorder, RoundedCornerShape(6.dp))
                            .clickable { viewModel.setDorkCategory(cat) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        items(filtered) { dork ->
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.launchGoogleDork(context, dork, targetDomainInput)
                }
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dork.title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        ThreatBadge(severity = dork.severity)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dork.description,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberObsidian)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = dork.dorkQuery,
                            color = TerminalGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "LAUNCH GOOGLE SEARCH ↗",
                            color = CrimsonLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7. EXIF & PIMEYES MEDIA FORENSICS VIEW
// -------------------------------------------------------------
@Composable
fun ExifForensicsToolView(viewModel: OsintViewModel) {
    val result by viewModel.exifResult.collectAsState()
    val isLoading by viewModel.isExifLoading.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.analyzeExifImage(uri)
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CyberButton(
                    text = "SELECT PHOTO",
                    onClick = { imagePickerLauncher.launch("image/*") },
                    icon = Icons.Default.PhotoCamera,
                    modifier = Modifier.weight(1f)
                )

                CyberButton(
                    text = "LOAD SAMPLE EXIF",
                    onClick = { viewModel.loadSampleExif() },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CrimsonPrimary)
                }
            }
        } else if (result != null) {
            val res = result!!
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "IMAGE METADATA & CAMERA PROVENANCE",
                            color = CrimsonPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        DataRow("File Name", res.fileName)
                        DataRow("Dimensions", res.dimensions)
                        DataRow("File Size", "${res.fileSizeKb} KB")
                        DataRow("Camera Device", "${res.cameraMake ?: ""} ${res.cameraModel ?: "Unknown"}")
                        DataRow("Date Captured", res.dateTaken ?: "N/A")
                        DataRow("Editing Software", res.software ?: "Direct Hardware Sensor")
                        DataRow("Lens / Exposure", "${res.focalLength ?: ""} ${res.exposureTime ?: ""} ${res.iso ?: ""}")
                    }
                }
            }

            item {
                TerminalBlock(
                    title = "GPS FORENSIC COORDINATES",
                    content = if (res.latitude != null && res.longitude != null) {
                        buildString {
                            appendLine("Geographic Coordinates Extracted:")
                            appendLine("  • Latitude: ${res.latitude}")
                            appendLine("  • Longitude: ${res.longitude}")
                            appendLine("  • Altitude: ${res.altitudeMeters} meters")
                            appendLine("  • Location: ${res.locationName}")
                            appendLine("\nTarget pinpointed in map registry.")
                        }
                    } else {
                        "No GPS coordinates found in image header (Cleaned/Stripped)."
                    },
                    accentColor = TerminalCyan
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberButton(
                        text = "PIMEYES REVERSE ↗",
                        onClick = {
                            viewModel.launchExternalUrl(context, "https://pimeyes.com")
                        },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )

                    if (res.latitude != null && res.longitude != null) {
                        CyberButton(
                            text = "VIEW ON MAP",
                            onClick = {
                                val mapUri = Uri.parse("geo:${res.latitude},${res.longitude}?q=${res.latitude},${res.longitude}(${res.fileName})")
                                val intent = Intent(Intent.ACTION_VIEW, mapUri)
                                context.startActivity(intent)
                            },
                            icon = Icons.Default.Launch,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchInputSection(
    label: String,
    placeholder: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onScan: () -> Unit,
    isLoading: Boolean,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
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
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(placeholder, color = TextMuted, fontSize = 12.sp, fontFamily = FontFamily.Monospace) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("${testTag}_input"),
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
                text = "EXECUTE",
                onClick = onScan,
                isLoading = isLoading,
                testTag = testTag
            )
        }
    }
}

@Composable
fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
