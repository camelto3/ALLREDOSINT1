package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ClassificationLevel(val label: String, val colorHex: Long) {
    UNCLASSIFIED("UNCLASSIFIED", 0xFF00E676),
    RESTRICTED("RESTRICTED", 0xFF00E5FF),
    CONFIDENTIAL("CONFIDENTIAL", 0xFFFF9100),
    SECRET("SECRET // NOFORN", 0xFFFF5252),
    TOP_SECRET("TOP SECRET // SCI", 0xFFFF1744)
}

enum class ThreatSeverity(val label: String, val priority: Int) {
    LOW("LOW", 1),
    MEDIUM("MEDIUM", 2),
    HIGH("HIGH", 3),
    CRITICAL("CRITICAL", 4)
}

enum class TargetCategory(val label: String) {
    DOMAIN("Domain & DNS"),
    IP_NETWORK("IP & Infrastructure"),
    PERSONA("Identity & Footprint"),
    EMAIL("Email & Breaches"),
    VULNERABILITY("CVE & Exploit"),
    METADATA("EXIF & Forensics"),
    GENERAL("General Intel")
}

@Entity(tableName = "dossiers")
data class DossierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val target: String,
    val title: String,
    val category: String,
    val classification: String,
    val threatScore: Int, // 0 - 100
    val encryptedPayload: String, // Encrypted with AES-256 GCM
    val tags: String, // Comma-separated
    val aiExecutiveSummary: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val category: String,
    val targetIdentifier: String,
    val source: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "watchlists")
data class WatchlistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetQuery: String,
    val targetType: String,
    val threatStatus: String,
    val tags: String,
    val lastScanTimestamp: Long = System.currentTimeMillis(),
    val alertMatches: Int = 0,
    val isMonitoringActive: Boolean = true
)

// In-memory UI Models

data class DnsRecordItem(
    val type: String,
    val value: String,
    val ttl: Int = 300
)

data class DnsLookupResult(
    val domain: String,
    val ipAddresses: List<String> = emptyList(),
    val mxRecords: List<String> = emptyList(),
    val nsRecords: List<String> = emptyList(),
    val txtRecords: List<String> = emptyList(),
    val cname: String? = null,
    val registrar: String = "Unknown",
    val createdDate: String = "N/A",
    val expiresDate: String = "N/A",
    val sslIssuer: String = "Let's Encrypt / DigiCert",
    val subdomainsDiscovered: List<String> = emptyList(),
    val riskScore: Int = 15,
    val rawPayload: String = ""
)

data class IpIntelResult(
    val ip: String,
    val hostname: String = "N/A",
    val city: String = "Unknown",
    val region: String = "Unknown",
    val country: String = "Global",
    val countryCode: String = "GL",
    val isp: String = "Tier-1 Carrier",
    val org: String = "Autonomous System",
    val asn: String = "AS00000",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val abuseConfidenceScore: Int = 0,
    val openPortsDetected: List<Int> = listOf(80, 443),
    val isTorNode: Boolean = false,
    val isVpnProxy: Boolean = false,
    val threatSummary: String = "Clean IP record. No malicious outbound scans detected in last 30 days."
)

data class FootprintPlatform(
    val name: String,
    val category: String,
    val profileUrlTemplate: String,
    val checkUrlTemplate: String,
    val iconName: String
)

data class FootprintHit(
    val platformName: String,
    val category: String,
    val profileUrl: String,
    val exists: Boolean,
    val statusText: String
)

data class EmailIntelResult(
    val email: String,
    val userPart: String,
    val domainPart: String,
    val isValidFormat: Boolean,
    val hasValidMx: Boolean,
    val isDisposable: Boolean,
    val gravatarUrl: String?,
    val breachCount: Int,
    val knownBreaches: List<String>,
    val riskLevel: String
)

data class CveItem(
    val cveId: String,
    val title: String,
    val cvssScore: Double,
    val severity: ThreatSeverity,
    val summary: String,
    val affectedProducts: List<String>,
    val attackVector: String,
    val publishedDate: String,
    val referenceUrl: String
)

data class GoogleDorkItem(
    val category: String,
    val title: String,
    val dorkQuery: String,
    val description: String,
    val severity: String
)

data class ExifForensicResult(
    val fileName: String,
    val fileSizeKb: Long,
    val dimensions: String,
    val cameraMake: String?,
    val cameraModel: String?,
    val dateTaken: String?,
    val latitude: Double?,
    val longitude: Double?,
    val altitudeMeters: Double?,
    val software: String?,
    val iso: String?,
    val focalLength: String?,
    val exposureTime: String?,
    val locationName: String?
)

data class AiAgentChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: AgentSender,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAudioPlaying: Boolean = false,
    val citations: List<String> = emptyList()
) {
    enum class AgentSender { USER, AGENT, SYSTEM }
}
