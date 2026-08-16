package com.example.service

import com.example.data.model.PhoneParseResult

class PhoneIntelService {

    fun parsePhoneNumber(raw: String): PhoneParseResult {
        val clean = raw.trim().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        val hasPlus = clean.startsWith("+")
        val digitsOnly = clean.removePrefix("+")

        val (countryCode, countryName, carrier, lineType, tz) = when {
            digitsOnly.startsWith("1") -> Tuple5("+1", "United States / Canada", "Verizon / AT&T / T-Mobile", "Mobile / VoIP", "UTC-5 to UTC-8 (EST/PST)")
            digitsOnly.startsWith("44") -> Tuple5("+44", "United Kingdom", "EE / Vodafone / O2 / Three", "Mobile / Geographic", "UTC+0 (GMT/BST)")
            digitsOnly.startsWith("49") -> Tuple5("+49", "Germany", "Deutsche Telekom / Vodafone", "Mobile / Fixed", "UTC+1 (CET)")
            digitsOnly.startsWith("33") -> Tuple5("+33", "France", "Orange / SFR / Bouygues", "Mobile / Fixed", "UTC+1 (CET)")
            digitsOnly.startsWith("61") -> Tuple5("+61", "Australia", "Telstra / Optus", "Mobile / Geographic", "UTC+8 to UTC+11 (AEST)")
            digitsOnly.startsWith("91") -> Tuple5("+91", "India", "Jio / Airtel / Vi", "Mobile Prepaid", "UTC+5:30 (IST)")
            digitsOnly.startsWith("81") -> Tuple5("+81", "Japan", "NTT Docomo / SoftBank", "Mobile", "UTC+9 (JST)")
            digitsOnly.startsWith("86") -> Tuple5("+86", "China", "China Mobile / Telecom", "Mobile", "UTC+8 (CST)")
            digitsOnly.startsWith("7") -> Tuple5("+7", "Russia / Kazakhstan", "MTS / MegaFon / Beeline", "Mobile", "UTC+3 (MSK)")
            digitsOnly.startsWith("55") -> Tuple5("+55", "Brazil", "Vivo / Claro / TIM", "Mobile", "UTC-3 (BRT)")
            else -> Tuple5(if (hasPlus) "+${digitsOnly.take(3)}" else "+1", "International Destination", "Local Autonomous Telecom Carrier", "Unknown / Cellular", "UTC Standard")
        }

        val e164 = if (hasPlus) clean else if (clean.length == 10 && !clean.startsWith("1")) "+1$clean" else "+$clean"

        val dorks = listOf(
            "\"$e164\"",
            "\"$raw\"",
            "site:truecaller.com \"$e164\"",
            "site:sync.me \"$e164\"",
            "site:facebook.com \"$e164\"",
            "site:linkedin.com \"$e164\"",
            "https://wa.me/${e164.removePrefix("+")}"
        )

        return PhoneParseResult(
            rawInput = raw,
            formattedE164 = e164,
            countryCode = countryCode,
            countryName = countryName,
            carrierEstimate = carrier,
            lineType = lineType,
            timeZone = tz,
            searchDorks = dorks,
            riskRating = if (lineType.contains("VoIP", ignoreCase = true)) "MODERATE (VoIP/Burner)" else "STANDARD CELLULAR"
        )
    }

    private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
}
