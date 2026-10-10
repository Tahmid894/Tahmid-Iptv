package com.example.data.model

data class Channel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val tvgId: String? = null,
    val tvgName: String? = null,
    val groupTitle: String? = null,
    val quality: String? = null, // e.g. "4K", "FHD", "HD", "SD"
    val country: String? = null,
    val language: String? = null,
    val sourcePlaylist: String? = null,
    val isFavorite: Boolean = false
) {
    val displayQuality: String?
        get() {
            if (!quality.isNullOrBlank()) return quality
            val lower = name.lowercase()
            return when {
                lower.contains("4k") || lower.contains("uhd") -> "4K"
                lower.contains("1080p") || lower.contains("fhd") || lower.contains("full hd") -> "FHD"
                lower.contains("720p") || lower.contains("hd") -> "HD"
                lower.contains("sd") || lower.contains("480p") -> "SD"
                else -> null
            }
        }
}
