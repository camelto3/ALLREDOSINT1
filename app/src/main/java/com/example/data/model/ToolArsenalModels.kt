package com.example.data.model

enum class ToolCategory(val title: String) {
    IDENTITY_PERSONA("Identity & Footprint"),
    BREACH_DARKNET("Breach & Threat Intelligence"),
    INFRASTRUCTURE_IOT("Infrastructure & Network"),
    CLOUD_AD("Cloud & Active Directory"),
    WEB_AUDIT_PROXY("Web Auditing & Scanning"),
    DATA_FORENSICS("Data Forensics & Cryptography")
}

data class ArsenalTool(
    val id: String,
    val name: String,
    val category: ToolCategory,
    val tagline: String,
    val description: String,
    val howToUse: String,
    val primarySyntax: String,
    val keyFeatures: List<String>,
    val riskOrDefensiveNote: String,
    val officialUrl: String,
    val iconType: String,
    val isInteractiveInApp: Boolean = true
)

data class PhoneParseResult(
    val rawInput: String,
    val formattedE164: String,
    val countryCode: String,
    val countryName: String,
    val carrierEstimate: String,
    val lineType: String,
    val timeZone: String,
    val searchDorks: List<String>,
    val riskRating: String
)
