package com.example.service

import android.util.Base64
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

enum class CyberChefOp(val title: String) {
    TO_BASE64("To Base64"),
    FROM_BASE64("From Base64"),
    TO_HEX("To Hex"),
    FROM_HEX("From Hex"),
    URL_ENCODE("URL Encode"),
    URL_DECODE("URL Decode"),
    ROT13("ROT13 Cipher"),
    SHA256("SHA-256 Hash"),
    MD5("MD5 Hash"),
    DEFANG_URL("Defang URL/IP (IOC Safe)"),
    REFANG_URL("Refang URL/IP"),
    EXTRACT_IPS("Extract IPv4 Addresses"),
    EXTRACT_EMAILS("Extract Emails"),
    EXTRACT_DOMAINS("Extract Hostnames"),
    JWT_DECODE("Decode JWT Claims"),
    REVERSE_TEXT("Reverse String")
}

class CyberChefEngine {

    fun executeRecipe(operation: CyberChefOp, input: String): String {
        if (input.isBlank()) return ""
        return try {
            when (operation) {
                CyberChefOp.TO_BASE64 -> Base64.encodeToString(input.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
                CyberChefOp.FROM_BASE64 -> String(Base64.decode(input.trim(), Base64.DEFAULT), StandardCharsets.UTF_8)
                CyberChefOp.TO_HEX -> input.toByteArray(StandardCharsets.UTF_8).joinToString("") { "%02x".format(it) }
                CyberChefOp.FROM_HEX -> {
                    val clean = input.replace(" ", "").replace("0x", "")
                    val bytes = ByteArray(clean.length / 2)
                    for (i in bytes.indices) {
                        val index = i * 2
                        bytes[i] = clean.substring(index, index + 2).toInt(16).toByte()
                    }
                    String(bytes, StandardCharsets.UTF_8)
                }
                CyberChefOp.URL_ENCODE -> URLEncoder.encode(input, "UTF-8")
                CyberChefOp.URL_DECODE -> URLDecoder.decode(input, "UTF-8")
                CyberChefOp.ROT13 -> rot13(input)
                CyberChefOp.SHA256 -> hash(input, "SHA-256")
                CyberChefOp.MD5 -> hash(input, "MD5")
                CyberChefOp.DEFANG_URL -> defang(input)
                CyberChefOp.REFANG_URL -> refang(input)
                CyberChefOp.EXTRACT_IPS -> extractIps(input)
                CyberChefOp.EXTRACT_EMAILS -> extractEmails(input)
                CyberChefOp.EXTRACT_DOMAINS -> extractDomains(input)
                CyberChefOp.JWT_DECODE -> decodeJwt(input)
                CyberChefOp.REVERSE_TEXT -> input.reversed()
            }
        } catch (e: Exception) {
            "ERROR: [${e.javaClass.simpleName}] ${e.message}"
        }
    }

    private fun rot13(input: String): String {
        val result = StringBuilder()
        for (char in input) {
            when (char) {
                in 'a'..'z' -> result.append(((char - 'a' + 13) % 26 + 'a'.code).toChar())
                in 'A'..'Z' -> result.append(((char - 'A' + 13) % 26 + 'A'.code).toChar())
                else -> result.append(char)
            }
        }
        return result.toString()
    }

    private fun hash(input: String, algorithm: String): String {
        val md = MessageDigest.getInstance(algorithm)
        val digest = md.digest(input.toByteArray(StandardCharsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun defang(input: String): String {
        return input
            .replace("http://", "hxxp://")
            .replace("https://", "hxxps://")
            .replace(".", "[.]")
            .replace("://", "[://]")
    }

    private fun refang(input: String): String {
        return input
            .replace("hxxps://", "https://")
            .replace("hxxp://", "http://")
            .replace("[.]", ".")
            .replace("[://]", "://")
    }

    private fun extractIps(input: String): String {
        val regex = Regex("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b")
        val matches = regex.findAll(input).map { it.value }.toSet()
        return if (matches.isEmpty()) "No IPv4 addresses identified." else matches.joinToString("\n")
    }

    private fun extractEmails(input: String): String {
        val regex = Regex("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+")
        val matches = regex.findAll(input).map { it.value }.toSet()
        return if (matches.isEmpty()) "No email addresses identified." else matches.joinToString("\n")
    }

    private fun extractDomains(input: String): String {
        val regex = Regex("\\b[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.(?:com|net|org|io|gov|edu|co|ai|dev|app|mil|xyz|tech|info)\\b", RegexOption.IGNORE_CASE)
        val matches = regex.findAll(input).map { it.value }.toSet()
        return if (matches.isEmpty()) "No hostnames identified." else matches.joinToString("\n")
    }

    private fun decodeJwt(input: String): String {
        val parts = input.trim().split(".")
        if (parts.size < 2) return "Invalid JWT format. Must contain at least header and payload separated by '.'."
        val header = try { String(Base64.decode(parts[0], Base64.URL_SAFE), StandardCharsets.UTF_8) } catch (e: Exception) { "Invalid Header Base64" }
        val payload = try { String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8) } catch (e: Exception) { "Invalid Payload Base64" }
        val signature = if (parts.size >= 3) parts[2] else "None"
        return """
=== JWT HEADER ===
$header

=== JWT PAYLOAD (CLAIMS) ===
$payload

=== SIGNATURE HASH ===
$signature
        """.trimIndent()
    }
}
