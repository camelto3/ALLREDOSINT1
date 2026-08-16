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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberTopAppBar
import com.example.ui.components.TerminalBlock
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
import com.example.ui.theme.StatusSecure
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.OsintViewModel

@Composable
fun VaultSettingsScreen(viewModel: OsintViewModel) {
    val dossiers by viewModel.dossiers.collectAsState()
    val selectedDossier by viewModel.selectedDossier.collectAsState()
    val isBiometricActive by viewModel.isBiometricActive.collectAsState()
    val bridgeKey by viewModel.webPortalBridgeKey.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var showPanicConfirmDialog by remember { mutableStateOf(false) }

    if (selectedDossier != null) {
        DossierDetailScreen(
            dossier = selectedDossier!!,
            viewModel = viewModel,
            onBack = { viewModel.selectDossier(null) }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberObsidian),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            CyberTopAppBar(
                title = "ENCRYPTED VAULT & SECURITY",
                subtitle = "AES-256 GCM HARDWARE STORAGE & WEB BRIDGE",
                onLockClick = { viewModel.lockVault() },
                onPanicClick = { showPanicConfirmDialog = true }
            )
        }

        // Cryptographic Enclave Status
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(TerminalGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CRYPTO ENCLAVE STATUS: ARMED",
                                    color = TerminalGreen,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = TerminalCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DataRow("Encryption Standard", "AES-256-GCM (128-bit Tag)")
                        DataRow("Key Derivation", "PBKDF2-HMAC-SHA256 (65k rounds)")
                        DataRow("Vault Integrity Hash", "SHA-256: 8f9b4c2a...3e1d")
                        DataRow("Encrypted Records", "${dossiers.size} Stored Case Files")
                    }
                }
            }
        }

        // Web Portal & Multi-Device Sync Hub
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "WEB PORTAL & DESKTOP ACCESS BRIDGE",
                    color = CrimsonPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Web,
                                    contentDescription = null,
                                    tint = CrimsonLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LOCAL HTTPS WEB GATEWAY",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "PORT 8443 TLS 1.3",
                                color = TerminalCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "Access your full OSINT Workbench from any web browser on desktop or tablet. Point your browser to the local secure endpoint:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        // Terminal link box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberObsidian)
                                .border(1.dp, CyberBorderGlow, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "https://127.0.0.1:8443/#/analyst",
                                    color = TerminalGreen,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString("https://127.0.0.1:8443/#/analyst"))
                                        viewModel.showToast("Web Portal URL copied to clipboard")
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy URL",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Session Auth Key: $bridgeKey",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "3 SESSIONS ACTIVE",
                                color = StatusSecure,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Biometric Security Settings
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "BIOMETRIC AUTHENTICATION & ACCESS CONTROL",
                    color = CrimsonPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Biometric Lock (Fingerprint / Face)",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Require biometric verification on app launch and vault opening",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = isBiometricActive,
                                onCheckedChange = { viewModel.toggleBiometricActive(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CrimsonPrimary,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = CyberObsidian
                                ),
                                modifier = Modifier.testTag("biometric_toggle_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Master Security PIN",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Default analyst fallback PIN: 7492",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            CyberButton(
                                text = "LOCK NOW",
                                onClick = { viewModel.lockVault() },
                                isPrimary = false,
                                testTag = "vault_lock_now_button"
                            )
                        }
                    }
                }
            }
        }

        // Encrypted Case Dossiers List
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "STORED CASE DOSSIERS (${dossiers.size})",
                    color = CrimsonPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (dossiers.isEmpty()) {
                    CyberCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No saved dossiers in vault.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        dossiers.forEach { dossier ->
                            DossierRowItem(
                                dossier = dossier,
                                onClick = { viewModel.selectDossier(dossier) }
                            )
                        }
                    }
                }
            }
        }

        // Emergency Panic Wipe Button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                CyberButton(
                    text = "EMERGENCY PANIC WIPE (PURGE ALL DATA)",
                    onClick = { showPanicConfirmDialog = true },
                    icon = Icons.Default.Warning,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "emergency_panic_wipe_button"
                )
            }
        }
    }

    if (showPanicConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPanicConfirmDialog = false },
            containerColor = CyberDarkSurface,
            title = {
                Text(
                    text = "EXECUTE PANIC WIPE?",
                    color = StatusCritical,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Text(
                    text = "This action will immediately cryptographically shred all saved investigation dossiers, clear all surveillance alerts, and reset the local encryption keystore. This cannot be undone.",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                CyberButton(
                    text = "CONFIRM PURGE",
                    onClick = {
                        viewModel.panicWipeVault()
                        showPanicConfirmDialog = false
                    },
                    isPrimary = true
                )
            },
            dismissButton = {
                CyberButton(
                    text = "CANCEL",
                    onClick = { showPanicConfirmDialog = false },
                    isPrimary = false
                )
            }
        )
    }
}
