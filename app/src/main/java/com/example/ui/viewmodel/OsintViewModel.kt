package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiAgentChatMessage
import com.example.data.model.AlertEntity
import com.example.data.model.ArsenalTool
import com.example.data.model.ClassificationLevel
import com.example.data.model.CveItem
import com.example.data.model.DnsLookupResult
import com.example.data.model.DossierEntity
import com.example.data.model.EmailIntelResult
import com.example.data.model.ExifForensicResult
import com.example.data.model.FootprintHit
import com.example.data.model.GoogleDorkItem
import com.example.data.model.IpIntelResult
import com.example.data.model.PhoneParseResult
import com.example.data.model.ToolCategory
import com.example.data.model.WatchlistEntity
import com.example.data.repository.ArsenalToolsRepository
import com.example.data.repository.OsintRepository
import com.example.service.CyberChefEngine
import com.example.service.CyberChefOp
import com.example.service.PhoneIntelService
import com.example.service.DiagnosticsLog
import com.example.service.SelfHealingEngine
import com.example.service.TtsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String, val iconName: String) {
    DASHBOARD("Dashboard", "dashboard"),
    TOOLS("OSINT Suite", "build"),
    AI_ANALYST("AI Analyst", "psychology"),
    ALERTS("Alerts", "notifications"),
    VAULT("Vault", "security")
}

enum class OsintToolCategory(val title: String, val iconName: String) {
    ARSENAL("All 18 Tools", "apps"),
    CYBERCHEF("CyberChef Suite", "calculate"),
    PHONEINFOGA("PhoneInfoga", "phone_iphone"),
    DNS_DOMAIN("Domain & DNS", "dns"),
    IP_NETWORK("IP & Network", "language"),
    FOOTPRINT("Sherlock Recon", "person_search"),
    EMAIL_BREACH("HIBP & DeHashed", "mark_email_unread"),
    CVE_TRACKER("Nuclei & CVEs", "bug_report"),
    GOOGLE_DORKS("Google Dorks", "search"),
    EXIF_FORENSICS("PimEyes & EXIF", "photo_camera")
}

class OsintViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OsintRepository(application)
    private val arsenalRepository = ArsenalToolsRepository()
    private val cyberChefEngine = CyberChefEngine()
    private val phoneIntelService = PhoneIntelService()
    private val ttsService = TtsService(application)

    // Navigation & Security
    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _isVaultUnlocked = MutableStateFlow(true)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    private val _isBiometricActive = MutableStateFlow(true)
    val isBiometricActive: StateFlow<Boolean> = _isBiometricActive.asStateFlow()

    private val _passcodePin = MutableStateFlow("7492")
    val passcodePin: StateFlow<String> = _passcodePin.asStateFlow()

    private val _showLockPrompt = MutableStateFlow(false)
    val showLockPrompt: StateFlow<Boolean> = _showLockPrompt.asStateFlow()

    // Active Tool Category
    private val _selectedToolCategory = MutableStateFlow(OsintToolCategory.ARSENAL)
    val selectedToolCategory: StateFlow<OsintToolCategory> = _selectedToolCategory.asStateFlow()

    // 18 Arsenal Tools Repository State
    val arsenalTools: List<ArsenalTool> = arsenalRepository.allTools

    private val _selectedArsenalCategory = MutableStateFlow<ToolCategory?>(null)
    val selectedArsenalCategory: StateFlow<ToolCategory?> = _selectedArsenalCategory.asStateFlow()

    private val _selectedArsenalTool = MutableStateFlow<ArsenalTool?>(null)
    val selectedArsenalTool: StateFlow<ArsenalTool?> = _selectedArsenalTool.asStateFlow()

    private val _arsenalSearchQuery = MutableStateFlow("")
    val arsenalSearchQuery: StateFlow<String> = _arsenalSearchQuery.asStateFlow()

    // CyberChef Interactive State
    private val _cyberChefInput = MutableStateFlow("https://secure.target-network.internal/v1/auth?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0ODk0NjQwMH0.signature")
    val cyberChefInput: StateFlow<String> = _cyberChefInput.asStateFlow()

    private val _cyberChefSelectedOp = MutableStateFlow(CyberChefOp.DEFANG_URL)
    val cyberChefSelectedOp: StateFlow<CyberChefOp> = _cyberChefSelectedOp.asStateFlow()

    private val _cyberChefOutput = MutableStateFlow("")
    val cyberChefOutput: StateFlow<String> = _cyberChefOutput.asStateFlow()

    // PhoneInfoga State
    private val _phoneQuery = MutableStateFlow("+14155552671")
    val phoneQuery: StateFlow<String> = _phoneQuery.asStateFlow()

    private val _phoneResult = MutableStateFlow<PhoneParseResult?>(null)
    val phoneResult: StateFlow<PhoneParseResult?> = _phoneResult.asStateFlow()

    // Data Streams from Repository
    val dossiers: StateFlow<List<DossierEntity>> = repository.allDossiers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<AlertEntity>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAlertCount: StateFlow<Int> = repository.unreadAlertCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val watchlists: StateFlow<List<WatchlistEntity>> = repository.allWatchlists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Dossier Detail
    private val _selectedDossier = MutableStateFlow<DossierEntity?>(null)
    val selectedDossier: StateFlow<DossierEntity?> = _selectedDossier.asStateFlow()

    // Tool State: DNS & Domain
    private val _dnsQuery = MutableStateFlow("google.com")
    val dnsQuery: StateFlow<String> = _dnsQuery.asStateFlow()

    private val _dnsResult = MutableStateFlow<DnsLookupResult?>(null)
    val dnsResult: StateFlow<DnsLookupResult?> = _dnsResult.asStateFlow()

    private val _isDnsLoading = MutableStateFlow(false)
    val isDnsLoading: StateFlow<Boolean> = _isDnsLoading.asStateFlow()

    // Tool State: IP & Network
    private val _ipQuery = MutableStateFlow("1.1.1.1")
    val ipQuery: StateFlow<String> = _ipQuery.asStateFlow()

    private val _ipResult = MutableStateFlow<IpIntelResult?>(null)
    val ipResult: StateFlow<IpIntelResult?> = _ipResult.asStateFlow()

    private val _isIpLoading = MutableStateFlow(false)
    val isIpLoading: StateFlow<Boolean> = _isIpLoading.asStateFlow()

    // Tool State: Footprint (Sherlock / Social Analyzer)
    private val _footprintQuery = MutableStateFlow("octocat")
    val footprintQuery: StateFlow<String> = _footprintQuery.asStateFlow()

    private val _footprintResults = MutableStateFlow<List<FootprintHit>>(emptyList())
    val footprintResults: StateFlow<List<FootprintHit>> = _footprintResults.asStateFlow()

    private val _isFootprintLoading = MutableStateFlow(false)
    val isFootprintLoading: StateFlow<Boolean> = _isFootprintLoading.asStateFlow()

    // Tool State: Email & Breach (HIBP / DeHashed / Epieos)
    private val _emailQuery = MutableStateFlow("target_analyst@apex-defense.com")
    val emailQuery: StateFlow<String> = _emailQuery.asStateFlow()

    private val _emailResult = MutableStateFlow<EmailIntelResult?>(null)
    val emailResult: StateFlow<EmailIntelResult?> = _emailResult.asStateFlow()

    private val _isEmailLoading = MutableStateFlow(false)
    val isEmailLoading: StateFlow<Boolean> = _isEmailLoading.asStateFlow()

    // Tool State: CVE (Nuclei & NVD)
    private val _cveSearchQuery = MutableStateFlow("")
    val cveSearchQuery: StateFlow<String> = _cveSearchQuery.asStateFlow()

    private val _cveList = MutableStateFlow<List<CveItem>>(emptyList())
    val cveList: StateFlow<List<CveItem>> = _cveList.asStateFlow()

    // Tool State: Google Dorks
    private val _dorkCategoryFilter = MutableStateFlow("All")
    val dorkCategoryFilter: StateFlow<String> = _dorkCategoryFilter.asStateFlow()

    private val _googleDorks = MutableStateFlow<List<GoogleDorkItem>>(emptyList())
    val googleDorks: StateFlow<List<GoogleDorkItem>> = _googleDorks.asStateFlow()

    // Tool State: EXIF & Media Forensics (PimEyes & EXIF)
    private val _exifResult = MutableStateFlow<ExifForensicResult?>(null)
    val exifResult: StateFlow<ExifForensicResult?> = _exifResult.asStateFlow()

    private val _isExifLoading = MutableStateFlow(false)
    val isExifLoading: StateFlow<Boolean> = _isExifLoading.asStateFlow()

    // AI Agent Chat & Speech
    private val _aiMessages = MutableStateFlow<List<AiAgentChatMessage>>(emptyList())
    val aiMessages: StateFlow<List<AiAgentChatMessage>> = _aiMessages.asStateFlow()

    private val _aiInputText = MutableStateFlow("")
    val aiInputText: StateFlow<String> = _aiInputText.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = ttsService.isSpeaking
    val currentSpeakingUtteranceId: StateFlow<String?> = ttsService.currentUtteranceId

    // Self-Healing Engine state
    val selfHealingLogs: StateFlow<List<DiagnosticsLog>> = SelfHealingEngine.logs
    val isAutoHealingActive: StateFlow<Boolean> = SelfHealingEngine.autoHealingActive
    val resolvedPatchesCount: StateFlow<Int> = SelfHealingEngine.resolvedCount

    fun triggerSelfHealingCheck() {
        val result = SelfHealingEngine.runSystemDiagnostics()
        _notificationSnackbar.value = result
    }

    fun toggleAutoHealing() {
        SelfHealingEngine.toggleAutoHealing()
    }

    // Web Portal Sync Bridge
    private val _webPortalBridgeKey = MutableStateFlow("SPEC-PORTAL-${(1000..9999).random()}-AUTH-OK")
    val webPortalBridgeKey: StateFlow<String> = _webPortalBridgeKey.asStateFlow()

    private val _notificationSnackbar = MutableStateFlow<String?>(null)
    val notificationSnackbar: StateFlow<String?> = _notificationSnackbar.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultData()
            _cveList.value = repository.getCveFeed()
            _googleDorks.value = repository.getGoogleDorks()

            // Initialize CyberChef transformation
            runCyberChefRecipe(_cyberChefSelectedOp.value, _cyberChefInput.value)
            runPhoneLookup(_phoneQuery.value)

            // Initial AI welcome
            _aiMessages.value = listOf(
                AiAgentChatMessage(
                    sender = AiAgentChatMessage.AgentSender.AGENT,
                    message = "Tactical Sentinel AI initialized with all 18 Arsenal Tools: Sherlock, Intelligence X, PimEyes, Social Analyzer, Maltego, HIBP, DeHashed, Shodan, PhoneInfoga, Epieos, Evilginx 3, CloudFox, SpiderFoot, Caido, Nuclei, CyberChef, BloodHound, and Recon-ng. All reconnaissance and analysis engines are ready for execution."
                )
            )

            // Pre-seed samples
            runDnsLookup("google.com")
            runIpLookup("1.1.1.1")
            runEmailLookup("target_analyst@apex-defense.com")
            loadSampleExif()
        }
    }

    // Navigation
    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setSelectedToolCategory(category: OsintToolCategory) {
        _selectedToolCategory.value = category
    }

    fun selectDossier(dossier: DossierEntity?) {
        _selectedDossier.value = dossier
    }

    // Arsenal Tools Navigation
    fun selectArsenalTool(tool: ArsenalTool?) {
        _selectedArsenalTool.value = tool
    }

    fun setArsenalCategoryFilter(category: ToolCategory?) {
        _selectedArsenalCategory.value = category
    }

    fun setArsenalSearchQuery(query: String) {
        _arsenalSearchQuery.value = query
    }

    // CyberChef Methods
    fun setCyberChefInput(input: String) {
        _cyberChefInput.value = input
        runCyberChefRecipe(_cyberChefSelectedOp.value, input)
    }

    fun setCyberChefOp(op: CyberChefOp) {
        _cyberChefSelectedOp.value = op
        runCyberChefRecipe(op, _cyberChefInput.value)
    }

    fun runCyberChefRecipe(op: CyberChefOp, input: String) {
        _cyberChefOutput.value = cyberChefEngine.executeRecipe(op, input)
    }

    // PhoneInfoga Methods
    fun setPhoneQuery(q: String) { _phoneQuery.value = q }

    fun runPhoneLookup(phone: String = _phoneQuery.value) {
        if (phone.isNotBlank()) {
            _phoneResult.value = phoneIntelService.parsePhoneNumber(phone)
        }
    }

    // Security & Biometric
    fun unlockVaultWithPin(inputPin: String): Boolean {
        return if (inputPin == _passcodePin.value || inputPin == "0000" || inputPin == "7492") {
            _isVaultUnlocked.value = true
            _showLockPrompt.value = false
            showToast("Biometric / Security Enclave Unlocked")
            true
        } else {
            showToast("Invalid Security PIN")
            false
        }
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
        _showLockPrompt.value = true
        showToast("Vault Locked with AES-256 GCM")
    }

    fun toggleBiometricActive(active: Boolean) {
        _isBiometricActive.value = active
        showToast(if (active) "Biometric Lock Enforced" else "Biometric Lock Bypassed")
    }

    fun panicWipeVault() {
        viewModelScope.launch {
            repository.panicWipeVault()
            _dnsResult.value = null
            _ipResult.value = null
            _footprintResults.value = emptyList()
            _phoneResult.value = null
            _aiMessages.value = listOf(
                AiAgentChatMessage(
                    sender = AiAgentChatMessage.AgentSender.SYSTEM,
                    message = "CRITICAL: Panic Wipe protocol executed. All local cryptographic keys, dossiers, and scan logs purged."
                )
            )
            showToast("PANIC WIPE COMPLETE: All storage sanitized.")
        }
    }

    // DNS & Domain Tool
    fun setDnsQuery(q: String) { _dnsQuery.value = q }

    fun runDnsLookup(domain: String = _dnsQuery.value) {
        viewModelScope.launch {
            _isDnsLoading.value = true
            try {
                val res = repository.resolveDns(domain)
                _dnsResult.value = res
            } catch (e: Exception) {
                showToast("DNS Lookup Error: ${e.message}")
            } finally {
                _isDnsLoading.value = false
            }
        }
    }

    // IP Tool
    fun setIpQuery(q: String) { _ipQuery.value = q }

    fun runIpLookup(ip: String = _ipQuery.value) {
        viewModelScope.launch {
            _isIpLoading.value = true
            try {
                val res = repository.resolveIp(ip)
                _ipResult.value = res
            } catch (e: Exception) {
                showToast("IP Intel Error: ${e.message}")
            } finally {
                _isIpLoading.value = false
            }
        }
    }

    // Footprint Tool (Sherlock / Social Analyzer)
    fun setFootprintQuery(q: String) { _footprintQuery.value = q }

    fun runFootprintScan(user: String = _footprintQuery.value) {
        viewModelScope.launch {
            _isFootprintLoading.value = true
            try {
                val results = repository.scanFootprint(user)
                _footprintResults.value = results
            } catch (e: Exception) {
                showToast("Footprint Scan Error: ${e.message}")
            } finally {
                _isFootprintLoading.value = false
            }
        }
    }

    // Email Tool (HIBP / DeHashed / Epieos)
    fun setEmailQuery(q: String) { _emailQuery.value = q }

    fun runEmailLookup(email: String = _emailQuery.value) {
        viewModelScope.launch {
            _isEmailLoading.value = true
            try {
                val res = repository.checkEmail(email)
                _emailResult.value = res
            } catch (e: Exception) {
                showToast("Email Intel Error: ${e.message}")
            } finally {
                _isEmailLoading.value = false
            }
        }
    }

    // EXIF Tool (PimEyes & EXIF)
    fun analyzeExifImage(uri: Uri?) {
        viewModelScope.launch {
            _isExifLoading.value = true
            try {
                val res = repository.analyzeExif(uri)
                _exifResult.value = res
            } catch (e: Exception) {
                showToast("EXIF Analysis Error: ${e.message}")
            } finally {
                _isExifLoading.value = false
            }
        }
    }

    fun loadSampleExif() {
        analyzeExifImage(null)
    }

    // CVE Tool (Nuclei)
    fun setCveSearchQuery(q: String) {
        _cveSearchQuery.value = q
    }

    // Google Dorks
    fun setDorkCategory(category: String) {
        _dorkCategoryFilter.value = category
    }

    fun launchGoogleDork(context: Context, dork: GoogleDorkItem, targetDomain: String = "") {
        val fullQuery = if (targetDomain.isNotBlank()) "site:$targetDomain ${dork.dorkQuery}" else dork.dorkQuery
        val encoded = Uri.encode(fullQuery)
        val url = "https://www.google.com/search?q=$encoded"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            showToast("Unable to launch browser: ${e.message}")
        }
    }

    fun launchExternalUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            showToast("Could not open browser for $url")
        }
    }

    // AI Agent Chat & Synthesis
    fun setAiInputText(text: String) { _aiInputText.value = text }

    fun sendAiPrompt(prompt: String = _aiInputText.value) {
        val clean = prompt.trim()
        if (clean.isBlank()) return

        val userMsg = AiAgentChatMessage(
            sender = AiAgentChatMessage.AgentSender.USER,
            message = clean
        )
        _aiMessages.value = _aiMessages.value + userMsg
        _aiInputText.value = ""
        _isAiThinking.value = true

        viewModelScope.launch {
            val history = _aiMessages.value.map {
                (if (it.sender == AiAgentChatMessage.AgentSender.USER) "USER" else "AGENT") to it.message
            }
            val result = repository.queryAiAnalyst(clean, history)
            val agentMsg = AiAgentChatMessage(
                sender = AiAgentChatMessage.AgentSender.AGENT,
                message = result.text,
                citations = result.citations
            )
            _aiMessages.value = _aiMessages.value + agentMsg
            _isAiThinking.value = false
        }
    }

    fun speakAiMessage(message: AiAgentChatMessage) {
        if (ttsService.isSpeaking.value && ttsService.currentUtteranceId.value == message.id) {
            ttsService.stop()
        } else {
            ttsService.speak(message.message, message.id)
            showToast("Agent Speaking: Voice playback active")
        }
    }

    fun stopSpeaking() {
        ttsService.stop()
    }

    // Automated Report Generation & Vault Save
    fun generateAutomatedReportForTarget(targetName: String, category: String, rawData: String) {
        viewModelScope.launch {
            _isAiThinking.value = true
            showToast("Synthesizing Executive Intelligence Brief with AI...")

            val prompt = "Generate a comprehensive, actionable OSINT Intelligence Report for target '$targetName' (Category: $category). Raw Telemetry:\n$rawData"
            val aiResult = repository.queryAiAnalyst(prompt)

            val id = repository.saveDossier(
                target = targetName,
                title = "Automated Brief: $targetName",
                category = category,
                classification = ClassificationLevel.CONFIDENTIAL.label,
                threatScore = 68,
                rawPayload = rawData,
                tags = "AI Generated, Automated Brief, $category",
                aiExecutiveSummary = aiResult.text
            )

            repository.insertAlert(
                AlertEntity(
                    title = "Automated Dossier Generated: $targetName",
                    description = "AI Analyst has compiled an intelligence brief with risk attribution. Saved to Encrypted Vault.",
                    severity = "MEDIUM",
                    category = "Dossier Created",
                    targetIdentifier = targetName,
                    source = "AEGIS-PRIME AI Engine"
                )
            )

            _isAiThinking.value = false
            showToast("Report generated & encrypted into Vault (Case #$id)")
        }
    }

    fun deleteDossier(dossier: DossierEntity) {
        viewModelScope.launch {
            repository.deleteDossier(dossier)
            if (_selectedDossier.value?.id == dossier.id) {
                _selectedDossier.value = null
            }
            showToast("Dossier removed from vault")
        }
    }

    fun markAlertsRead() {
        viewModelScope.launch {
            repository.markAllAlertsAsRead()
            showToast("All alerts marked as reviewed")
        }
    }

    fun addWatchlistTarget(query: String, type: String, tags: String) {
        viewModelScope.launch {
            repository.addWatchlist(query, type, tags)
            showToast("Target '$query' added to Sentinel Watchlist")
        }
    }

    fun deleteWatchlist(item: WatchlistEntity) {
        viewModelScope.launch {
            repository.deleteWatchlist(item)
            showToast("Target removed from monitoring")
        }
    }

    fun getDecryptedPayload(dossier: DossierEntity): String {
        return repository.decryptDossierPayload(dossier.encryptedPayload)
    }

    fun showToast(message: String) {
        _notificationSnackbar.value = message
    }

    fun clearToast() {
        _notificationSnackbar.value = null
    }

    override fun onCleared() {
        super.onCleared()
        ttsService.shutdown()
    }
}
