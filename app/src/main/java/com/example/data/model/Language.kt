package com.example.data.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val playlistUrl: String
) {
    companion object {
        val ALL_LANGUAGES = listOf(
            Language("eng", "English", "English", "https://iptv-org.github.io/iptv/languages/eng.m3u"),
            Language("ben", "Bengali", "বাংলা", "https://iptv-org.github.io/iptv/languages/ben.m3u"),
            Language("ara", "Arabic", "العربية", "https://iptv-org.github.io/iptv/languages/ara.m3u"),
            Language("spa", "Spanish", "Español", "https://iptv-org.github.io/iptv/languages/spa.m3u"),
            Language("deu", "German", "Deutsch", "https://iptv-org.github.io/iptv/languages/deu.m3u"),
            Language("fra", "French", "Français", "https://iptv-org.github.io/iptv/languages/fra.m3u"),
            Language("rus", "Russian", "Русский", "https://iptv-org.github.io/iptv/languages/rus.m3u"),
            Language("por", "Portuguese", "Português", "https://iptv-org.github.io/iptv/languages/por.m3u"),
            Language("hin", "Hindi", "हिन्दी", "https://iptv-org.github.io/iptv/languages/hin.m3u"),
            Language("tur", "Turkish", "Türkçe", "https://iptv-org.github.io/iptv/languages/tur.m3u"),
            Language("ita", "Italian", "Italiano", "https://iptv-org.github.io/iptv/languages/ita.m3u"),
            Language("tam", "Tamil", "தமிழ்", "https://iptv-org.github.io/iptv/languages/tam.m3u"),
            Language("tel", "Telugu", "తెలుగు", "https://iptv-org.github.io/iptv/languages/tel.m3u"),
            Language("guj", "Gujarati", "ગુજરાતી", "https://iptv-org.github.io/iptv/languages/guj.m3u"),
            Language("pol", "Polish", "Polski", "https://iptv-org.github.io/iptv/languages/pol.m3u"),
            Language("ukr", "Ukrainian", "Українська", "https://iptv-org.github.io/iptv/languages/ukr.m3u"),
            Language("ell", "Greek", "Ελληνικά", "https://iptv-org.github.io/iptv/languages/ell.m3u"),
            Language("ron", "Romanian", "Română", "https://iptv-org.github.io/iptv/languages/ron.m3u"),
            Language("swe", "Swedish", "Svenska", "https://iptv-org.github.io/iptv/languages/swe.m3u"),
            Language("tgl", "Tagalog", "Tagalog", "https://iptv-org.github.io/iptv/languages/tgl.m3u"),
            Language("tha", "Thai", "ไทย", "https://iptv-org.github.io/iptv/languages/tha.m3u"),
            Language("vie", "Vietnamese", "Tiếng Việt", "https://iptv-org.github.io/iptv/languages/vie.m3u"),
            Language("yue", "Yue Chinese (Cantonese)", "粵語", "https://iptv-org.github.io/iptv/languages/yue.m3u"),
            Language("fas", "Persian", "فارسی", "https://iptv-org.github.io/iptv/languages/fas.m3u"),
            Language("pus", "Pashto", "پښتو", "https://iptv-org.github.io/iptv/languages/pus.m3u"),
            Language("mon", "Mongolian", "Монгол", "https://iptv-org.github.io/iptv/languages/mon.m3u")
        )
    }
}
