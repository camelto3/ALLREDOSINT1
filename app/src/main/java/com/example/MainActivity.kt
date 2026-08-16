package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AiAnalystScreen
import com.example.ui.screens.AlertsWatchlistScreen
import com.example.ui.screens.BiometricLockScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OsintToolsScreen
import com.example.ui.screens.VaultSettingsScreen
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonLight
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberObsidian
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.OsintViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: OsintViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SpectreOsintApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SpectreOsintApp(viewModel: OsintViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val unreadAlerts by viewModel.unreadAlertCount.collectAsState()
    val notificationMsg by viewModel.notificationSnackbar.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(notificationMsg) {
        if (notificationMsg != null) {
            snackbarHostState.showSnackbar(notificationMsg!!)
            viewModel.clearToast()
        }
    }

    if (!isVaultUnlocked) {
        BiometricLockScreen(
            onUnlockWithPin = { pin -> viewModel.unlockVaultWithPin(pin) },
            onBypassForDemo = { viewModel.unlockVaultWithPin("7492") }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberObsidian,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = CyberDarkSurface,
                    contentColor = TextPrimary,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .border(1.dp, CrimsonPrimary, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = data.visuals.message,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CyberDarkSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = CyberBorder, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val tabs = listOf(
                    Triple(AppNavTab.DASHBOARD, Icons.Default.Dashboard, "Dashboard"),
                    Triple(AppNavTab.TOOLS, Icons.Default.Build, "OSINT Tools"),
                    Triple(AppNavTab.AI_ANALYST, Icons.Default.Psychology, "AI Agent"),
                    Triple(AppNavTab.ALERTS, Icons.Default.Notifications, "Alerts"),
                    Triple(AppNavTab.VAULT, Icons.Default.Security, "Vault")
                )

                tabs.forEach { (tab, icon, label) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
                        icon = {
                            if (tab == AppNavTab.ALERTS && unreadAlerts > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = StatusCritical,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = "$unreadAlerts", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = label.uppercase(),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonPrimary,
                            selectedTextColor = CrimsonPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CrimsonDark.copy(alpha = 0.35f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppNavTab.TOOLS -> OsintToolsScreen(viewModel = viewModel)
                AppNavTab.AI_ANALYST -> AiAnalystScreen(viewModel = viewModel)
                AppNavTab.ALERTS -> AlertsWatchlistScreen(viewModel = viewModel)
                AppNavTab.VAULT -> VaultSettingsScreen(viewModel = viewModel)
            }
        }
    }
}
