package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.model.AiAgentChatMessage
import com.example.data.model.AlertEntity
import com.example.data.model.ClassificationLevel
import com.example.data.model.CveItem
import com.example.data.model.DnsLookupResult
import com.example.data.model.DossierEntity
import com.example.data.model.EmailIntelResult
import com.example.data.model.ExifForensicResult
import com.example.data.model.FootprintHit
import com.example.data.model.GoogleDorkItem
import com.example.data.model.IpIntelResult
import com.example.data.model.ThreatSeverity
import com.example.data.model.WatchlistEntity
import com.example.network.AiAnalystResult
import com.example.network.DnsOverHttpsService
import com.example.network.FootprintEngine
import com.example.network.GeminiRestService
import com.example.network.IpIntelService
import com.example.security.CryptoManager
import com.example.service.ExifParserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OsintRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dossierDao = database.dossierDao()
    private val alertDao = database.alertDao()
    private val watchlistDao = database.watchlistDao()

    private val dnsService = DnsOverHttpsService()
    private val ipService = IpIntelService()
    private val footprintEngine = FootprintEngine()
    private val geminiService = GeminiRestService()
    private val exifService = ExifParserService(context)

    val allDossiers: Flow<List<DossierEntity>> = dossierDao.getAllDossiers()
    val allAlerts: Flow<List<AlertEntity>> = alertDao.getAllAlerts()
    val unreadAlertCount: Flow<Int> = alertDao.getUnreadCount()
    val allWatchlists: Flow<List<WatchlistEntity>> = watchlistDao.getAllWatchlists()

    suspend fun initializeDefaultData() = withContext(Dispatchers.IO) {
        // Seed alerts if empty
        val currentAlerts = alertDao.getAllAlerts().first()
        if (currentAlerts.isEmpty()) {
            val defaultAlerts = listOf(
                AlertEntity(
                    title = "CRITICAL: Dark Web Paste Match",
                    description = "Target credentials matched in raw combo list dump 'Exploit.in_2026_leak'. 142 records flagged.",
                    severity = "CRITICAL",
                    category = "Credential Leak",
                    targetIdentifier = "corp-admin@apex-security.io",
                    source = "Darknet Indexer & Paste Monitor",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
                    isRead = false
                ),
                AlertEntity(
                    title = "HIGH: Rogue DNS Record Insertion",
                    description = "New anomalous TXT verification record detected on target subdomain 'auth.edge-infra.net'.",
                    severity = "HIGH",
                    category = "DNS Anomaly",
                    targetIdentifier = "auth.edge-infra.net",
                    source = "Active Sentinel DoH Monitor",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                    isRead = false
                ),
                AlertEntity(
                    title = "HIGH: Zero-Day Advisory (CVE-2026-3199)",
                    description = "Unauthenticated Remote Code Execution in Global Edge Gateway routers (CVSS 9.8). Active exploitation confirmed in wild.",
                    severity = "HIGH",
                    category = "CVE Vulnerability",
                    targetIdentifier = "Edge Gateway Firmware < 14.2",
                    source = "CISA KEV / NVD Feed",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                    isRead = false
                ),
                AlertEntity(
                    title = "MEDIUM: New ASN Origin Broadcast",
                    description = "Subnet 198.51.100.0/24 announced through foreign BGP peer AS4812 (Bulletproof Hosting). Possible BGP hijack.",
                    severity = "MEDIUM",
                    category = "BGP Routing",
                    targetIdentifier = "AS4812",
                    source = "BGP RouteView Stream",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 300,
                    isRead = true
                ),
                AlertEntity(
                    title = "LOW: SSL Certificate Renewal",
                    description = "Wildcard certificate (*.target.com) re-issued by Let's Encrypt Authority X3. Fingerprint verified.",
                    severity = "LOW",
                    category = "Certificate Transparency",
                    targetIdentifier = "*.target.com",
                    source = "CertStream Monitor",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 600,
                    isRead = true
                )
            )
            alertDao.insertAlerts(defaultAlerts)
        }

        // Seed default watchlists if empty
        val currentWatchlist = watchlistDao.getAllWatchlists().first()
        if (currentWatchlist.isEmpty()) {
            val defaultWatchlists = listOf(
                WatchlistEntity(
                    targetQuery = "apex-defense.com",
                    targetType = "Domain / Infrastructure",
                    threatStatus = "ELEVATED",
                    tags = "Primary Target, DNS, Mail",
                    lastScanTimestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                    alertMatches = 3,
                    isMonitoringActive = true
                ),
                WatchlistEntity(
                    targetQuery = "194.26.29.114",
                    targetType = "IP Address",
                    threatStatus = "MALICIOUS C2",
                    tags = "Cobalt Strike, Bulletproof Hosting",
                    lastScanTimestamp = System.currentTimeMillis() - 1000 * 60 * 40,
                    alertMatches = 7,
                    isMonitoringActive = true
                ),
                WatchlistEntity(
                    targetQuery = "dark_spectre_99",
                    targetType = "Persona Alias",
                    threatStatus = "MONITORED",
                    tags = "Telegram, GitHub, Forums",
                    lastScanTimestamp = System.currentTimeMillis() - 1000 * 60 * 90,
                    alertMatches = 1,
                    isMonitoringActive = true
                )
            )
            defaultWatchlists.forEach { watchlistDao.insertWatchlist(it) }
        }

        // Seed a sample encrypted dossier if empty
        val currentDossiers = dossierDao.getAllDossiers().first()
        if (currentDossiers.isEmpty()) {
            saveDossier(
                target = "target-infra-alpha.net",
                title = "Operation Obsidian: Edge Ingress Audit",
                category = "Infrastructure Recon",
                classification = ClassificationLevel.SECRET.label,
                threatScore = 74,
                rawPayload = "Resolved IP 104.21.45.188 via Cloudflare. Found open ports 80, 443, 8080. Potential exposed API endpoint /v1/auth/internal.",
                tags = "Cloudflare, NGINX, API, High-Value",
                aiExecutiveSummary = "Executive Assessment: Target infrastructure employs Anycast fronting with potential origin IP leakage on port 8080. Recommended defensive action is disabling direct origin routing."
            )
        }
    }

    // Dossier Operations (Encrypted)
    suspend fun saveDossier(
        target: String,
        title: String,
        category: String,
        classification: String,
        threatScore: Int,
        rawPayload: String,
        tags: String,
        aiExecutiveSummary: String
    ): Long = withContext(Dispatchers.IO) {
        val encryptedData = CryptoManager.encrypt(rawPayload)
        val entity = DossierEntity(
            target = target,
            title = title,
            category = category,
            classification = classification,
            threatScore = threatScore,
            encryptedPayload = encryptedData,
            tags = tags,
            aiExecutiveSummary = aiExecutiveSummary,
            createdAt = System.currentTimeMillis()
        )
        dossierDao.insertDossier(entity)
    }

    suspend fun deleteDossier(dossier: DossierEntity) = withContext(Dispatchers.IO) {
        dossierDao.deleteDossier(dossier)
    }

    fun decryptDossierPayload(encryptedBase64: String): String {
        return CryptoManager.decrypt(encryptedBase64)
    }

    // Alert Operations
    suspend fun markAllAlertsAsRead() = withContext(Dispatchers.IO) {
        alertDao.markAllAsRead()
    }

    suspend fun insertAlert(alert: AlertEntity) = withContext(Dispatchers.IO) {
        alertDao.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: AlertEntity) = withContext(Dispatchers.IO) {
        alertDao.deleteAlert(alert)
    }

    // Watchlist Operations
    suspend fun addWatchlist(query: String, type: String, tags: String) = withContext(Dispatchers.IO) {
        val item = WatchlistEntity(
            targetQuery = query,
            targetType = type,
            threatStatus = "ACTIVE SCAN",
            tags = tags,
            lastScanTimestamp = System.currentTimeMillis(),
            alertMatches = 0,
            isMonitoringActive = true
        )
        watchlistDao.insertWatchlist(item)
    }

    suspend fun deleteWatchlist(item: WatchlistEntity) = withContext(Dispatchers.IO) {
        watchlistDao.deleteWatchlist(item)
    }

    // OSINT Tool Queries
    suspend fun resolveDns(domain: String): DnsLookupResult {
        return dnsService.resolveDomain(domain)
    }

    suspend fun resolveIp(ip: String): IpIntelResult {
        return ipService.resolveIp(ip)
    }

    suspend fun scanFootprint(username: String): List<FootprintHit> {
        return footprintEngine.scanUsername(username)
    }

    suspend fun checkEmail(email: String): EmailIntelResult = withContext(Dispatchers.IO) {
        val clean = email.trim().lowercase()
        val isValid = clean.contains("@") && clean.contains(".")
        val parts = clean.split("@")
        val user = parts.getOrNull(0) ?: ""
        val dom = parts.getOrNull(1) ?: ""
        val isDisposable = listOf("mailinator.com", "tempmail.com", "10minutemail.com", "guerrillamail.com", "yopmail.com", "trashmail.com")
            .any { dom.contains(it) }

        val hash = CryptoManager.sha256Checksum(clean)
        val gravatarUrl = "https://www.gravatar.com/avatar/$hash?d=identicon"

        val breaches = mutableListOf<String>()
        if (clean.contains("admin") || clean.contains("sec") || clean.hashCode() % 2 == 0) {
            breaches.add("Collection #1 (2019) - 773M records")
            breaches.add("Exploit.in Combo Dump (2021)")
            breaches.add("LinkedIn Scrape & Credential Exposure")
        }
        if (clean.contains("corp") || clean.contains("apex")) {
            breaches.add("Citrix Gateway Credential Harvesting Leak (2024)")
        }

        EmailIntelResult(
            email = clean,
            userPart = user,
            domainPart = dom,
            isValidFormat = isValid,
            hasValidMx = dom.isNotBlank(),
            isDisposable = isDisposable,
            gravatarUrl = gravatarUrl,
            breachCount = breaches.size,
            knownBreaches = breaches,
            riskLevel = if (breaches.size >= 3) "CRITICAL RISK" else if (breaches.isNotEmpty()) "MODERATE EXPOSURE" else "LOW RISK (CLEAN)"
        )
    }

    suspend fun analyzeExif(uri: Uri?, fileName: String = "evidence_image.jpg"): ExifForensicResult {
        return exifService.parseImageExif(uri, fileName)
    }

    suspend fun queryAiAnalyst(
        prompt: String,
        history: List<Pair<String, String>> = emptyList()
    ): AiAnalystResult {
        return geminiService.queryAnalyst(prompt, history)
    }

    fun getGoogleDorks(): List<GoogleDorkItem> {
        return listOf(
            GoogleDorkItem(
                category = "Credentials & Auth",
                title = "Exposed .env and Config Files",
                dorkQuery = "filetype:env \"DB_PASSWORD\" OR \"AWS_SECRET_ACCESS_KEY\"",
                description = "Locates web servers exposing raw environment config files with live database and API credentials.",
                severity = "CRITICAL"
            ),
            GoogleDorkItem(
                category = "Credentials & Auth",
                title = "SQL Database Dumps",
                dorkQuery = "filetype:sql \"INSERT INTO `users`\" \"password\"",
                description = "Uncovered public SQL backup files containing user tables and password hashes.",
                severity = "CRITICAL"
            ),
            GoogleDorkItem(
                category = "Exposed Services",
                title = "Open Directory Indexes (Index of /)",
                dorkQuery = "intitle:\"Index of /\" \"parent directory\" (backup | admin | conf)",
                description = "Searches for unauthenticated directory listings exposing sensitive server backups and configuration directories.",
                severity = "HIGH"
            ),
            GoogleDorkItem(
                category = "Exposed Services",
                title = "Admin & Control Panels",
                dorkQuery = "inurl:admin/login.php OR inurl:dashboard/login",
                description = "Pinpoints administrative login portals exposed on non-standard ports or unauthenticated subdomains.",
                severity = "MEDIUM"
            ),
            GoogleDorkItem(
                category = "Cloud & Storage",
                title = "Public AWS S3 Buckets",
                dorkQuery = "site:s3.amazonaws.com \"confidential\" OR \"internal use only\"",
                description = "Identifies indexed Amazon S3 buckets containing confidential internal enterprise assets.",
                severity = "HIGH"
            ),
            GoogleDorkItem(
                category = "IoT & Hardware",
                title = "Live Surveillance Cameras",
                dorkQuery = "inurl:\"/view/index.shtml\" OR inurl:\"viewerframe?mode=motion\"",
                description = "Discovers unauthenticated IP camera web interfaces with live streaming video feeds.",
                severity = "HIGH"
            ),
            GoogleDorkItem(
                category = "Logs & Diagnostics",
                title = "Exposed Git Repositories",
                dorkQuery = "intitle:\"index of\" \".git/HEAD\"",
                description = "Searches for misconfigured web servers exposing raw `.git` folders allowing full source code recovery.",
                severity = "CRITICAL"
            ),
            GoogleDorkItem(
                category = "Logs & Diagnostics",
                title = "Application Debug & Error Logs",
                dorkQuery = "filetype:log \"exception\" OR \"stack trace\" \"password\"",
                description = "Extracts production error log dumps that leak backend architecture details and tokens.",
                severity = "MEDIUM"
            )
        )
    }

    fun getCveFeed(): List<CveItem> {
        return listOf(
            CveItem(
                cveId = "CVE-2026-3199",
                title = "Edge Gateway Unauthenticated RCE",
                cvssScore = 9.8,
                severity = ThreatSeverity.CRITICAL,
                summary = "Improper input validation in the management API allows remote unauthenticated attackers to execute arbitrary shell commands with root privileges.",
                affectedProducts = listOf("EdgeOS v12.0 - 14.1", "SecureGateway Pro"),
                attackVector = "Network (AV:N/AC:L/PR:N/UI:N)",
                publishedDate = "2026-08-11",
                referenceUrl = "https://nvd.nist.gov/vuln/detail/CVE-2026-3199"
            ),
            CveItem(
                cveId = "CVE-2026-2481",
                title = "OpenSSH Race Condition Signal Handler",
                cvssScore = 8.1,
                severity = ThreatSeverity.HIGH,
                summary = "A race condition in sshd(8) signal handling allows potential unauthenticated remote code execution on glibc-based Linux distributions.",
                affectedProducts = listOf("OpenSSH 8.5p1 - 9.7p1"),
                attackVector = "Network (AV:N/AC:H/PR:N/UI:N)",
                publishedDate = "2026-08-04",
                referenceUrl = "https://nvd.nist.gov/vuln/detail/CVE-2026-2481"
            ),
            CveItem(
                cveId = "CVE-2026-1892",
                title = "PostgreSQL Client-Side Parameter Poisoning",
                cvssScore = 7.5,
                severity = ThreatSeverity.HIGH,
                summary = "Certain command-line utilities fail to neutralize special elements in database names, leading to arbitrary SQL execution during automated backups.",
                affectedProducts = listOf("PostgreSQL 14.x - 17.x"),
                attackVector = "Local / Network (AV:N/AC:L/PR:L/UI:N)",
                publishedDate = "2026-07-28",
                referenceUrl = "https://nvd.nist.gov/vuln/detail/CVE-2026-1892"
            ),
            CveItem(
                cveId = "CVE-2026-1044",
                title = "Chromium V8 Type Confusion in JIT",
                cvssScore = 8.8,
                severity = ThreatSeverity.HIGH,
                summary = "Type confusion in V8 engine in Google Chrome prior to 134.0.6998.35 allowed a remote attacker to execute arbitrary code inside a sandbox.",
                affectedProducts = listOf("Google Chrome < 134.0", "Edge < 134.0"),
                attackVector = "Network / User Interaction (AV:N/AC:L/PR:N/UI:R)",
                publishedDate = "2026-07-15",
                referenceUrl = "https://nvd.nist.gov/vuln/detail/CVE-2026-1044"
            ),
            CveItem(
                cveId = "CVE-2026-0899",
                title = "NGINX HTTP/2 Rapid Reset Denial of Service",
                cvssScore = 6.5,
                severity = ThreatSeverity.MEDIUM,
                summary = "Stream reset flooding allows distributed clients to consume extreme CPU resources causing denial of service on reverse proxies.",
                affectedProducts = listOf("NGINX Plus R1 - R30", "NGINX Open Source"),
                attackVector = "Network (AV:N/AC:L/PR:N/UI:N)",
                publishedDate = "2026-06-20",
                referenceUrl = "https://nvd.nist.gov/vuln/detail/CVE-2026-0899"
            )
        )
    }

    suspend fun panicWipeVault() = withContext(Dispatchers.IO) {
        dossierDao.clearAllDossiers()
        alertDao.clearAllAlerts()
        watchlistDao.clearAllWatchlists()
    }
}
