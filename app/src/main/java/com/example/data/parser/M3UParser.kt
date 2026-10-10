package com.example.data.parser

import com.example.data.model.Channel
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID
import java.util.regex.Pattern

object M3UParser {

    private val TVG_ID_PATTERN = Pattern.compile("tvg-id=[\"']?([^\"',]*)[\"']?", Pattern.CASE_INSENSITIVE)
    private val TVG_NAME_PATTERN = Pattern.compile("tvg-name=[\"']?([^\"',]*)[\"']?", Pattern.CASE_INSENSITIVE)
    private val TVG_LOGO_PATTERN = Pattern.compile("tvg-logo=[\"']?([^\"',]*)[\"']?", Pattern.CASE_INSENSITIVE)
    private val GROUP_TITLE_PATTERN = Pattern.compile("group-title=[\"']?([^\"',]*)[\"']?", Pattern.CASE_INSENSITIVE)
    private val TVG_COUNTRY_PATTERN = Pattern.compile("tvg-country=[\"']?([^\"',]*)[\"']?", Pattern.CASE_INSENSITIVE)
    private val TVG_LANGUAGE_PATTERN = Pattern.compile("tvg-language=[\"']?([^\"',]*)[\"']?", Pattern.CASE_INSENSITIVE)

    /**
     * Parses an M3U / M3U8 playlist input stream into a list of Channel objects.
     */
    fun parse(inputStream: InputStream, sourceName: String? = null): List<Channel> {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        val channels = mutableListOf<Channel>()

        var currentTvgId: String? = null
        var currentTvgName: String? = null
        var currentLogoUrl: String? = null
        var currentGroupTitle: String? = null
        var currentCountry: String? = null
        var currentLanguage: String? = null
        var currentTitle: String? = null

        reader.useLines { lines ->
            for (rawLine in lines) {
                val line = rawLine.trim()
                if (line.isEmpty()) continue

                if (line.startsWith("#EXTINF:", ignoreCase = true)) {
                    // Extract attributes
                    currentTvgId = extractAttribute(TVG_ID_PATTERN, line)
                    currentTvgName = extractAttribute(TVG_NAME_PATTERN, line)
                    currentLogoUrl = extractAttribute(TVG_LOGO_PATTERN, line)
                    currentGroupTitle = extractAttribute(GROUP_TITLE_PATTERN, line)
                    currentCountry = extractAttribute(TVG_COUNTRY_PATTERN, line)
                    currentLanguage = extractAttribute(TVG_LANGUAGE_PATTERN, line)

                    // Extract title after comma
                    val commaIndex = line.lastIndexOf(',')
                    currentTitle = if (commaIndex != -1 && commaIndex < line.length - 1) {
                        line.substring(commaIndex + 1).trim()
                    } else {
                        currentTvgName ?: "Unknown Channel"
                    }
                } else if (line.startsWith("#EXTGRP:", ignoreCase = true)) {
                    // Group title alternative
                    if (currentGroupTitle.isNullOrBlank()) {
                        currentGroupTitle = line.substringAfter(":").trim()
                    }
                } else if (!line.startsWith("#")) {
                    // This is a stream URL!
                    val streamUrl = line.trim()
                    if (streamUrl.startsWith("http://", ignoreCase = true) ||
                        streamUrl.startsWith("https://", ignoreCase = true) ||
                        streamUrl.startsWith("rtmp://", ignoreCase = true) ||
                        streamUrl.startsWith("mms://", ignoreCase = true)
                    ) {
                        val finalName = currentTitle?.takeIf { it.isNotBlank() }
                            ?: currentTvgName?.takeIf { it.isNotBlank() }
                            ?: "Channel ${channels.size + 1}"

                        val quality = detectQuality(finalName, streamUrl)
                        val channelId = generateChannelId(currentTvgId, streamUrl, finalName)

                        channels.add(
                            Channel(
                                id = channelId,
                                name = finalName,
                                streamUrl = streamUrl,
                                logoUrl = currentLogoUrl?.takeIf { it.isNotBlank() },
                                tvgId = currentTvgId?.takeIf { it.isNotBlank() },
                                tvgName = currentTvgName?.takeIf { it.isNotBlank() },
                                groupTitle = currentGroupTitle?.takeIf { it.isNotBlank() },
                                quality = quality,
                                country = currentCountry,
                                language = currentLanguage,
                                sourcePlaylist = sourceName
                            )
                        )
                    }

                    // Reset current item state
                    currentTvgId = null
                    currentTvgName = null
                    currentLogoUrl = null
                    currentGroupTitle = null
                    currentCountry = null
                    currentLanguage = null
                    currentTitle = null
                }
            }
        }

        return channels
    }

    private fun extractAttribute(pattern: Pattern, line: String): String? {
        val matcher = pattern.matcher(line)
        return if (matcher.find()) {
            val value = matcher.group(1)?.trim()
            if (!value.isNullOrBlank()) value else null
        } else null
    }

    private fun detectQuality(name: String, url: String): String? {
        val combined = "$name $url".lowercase()
        return when {
            combined.contains("4k") || combined.contains("2160p") || combined.contains("uhd") -> "4K"
            combined.contains("1080p") || combined.contains("fhd") || combined.contains("full hd") -> "FHD"
            combined.contains("720p") || combined.contains("hd") -> "HD"
            combined.contains("480p") || combined.contains("sd") -> "SD"
            else -> null
        }
    }

    private fun generateChannelId(tvgId: String?, url: String, name: String): String {
        return if (!tvgId.isNullOrBlank()) {
            tvgId.trim().lowercase().replace("[^a-z0-9]".toRegex(), "_")
        } else {
            "${name.hashCode()}_${url.hashCode()}".replace("-", "n")
        }
    }
}
