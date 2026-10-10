package com.example.data.model

data class Country(
    val code: String,
    val name: String,
    val flagEmoji: String
) {
    val playlistUrl: String
        get() = "https://iptv-org.github.io/iptv/countries/${code.lowercase()}.m3u"

    companion object {
        fun flagEmojiFromCode(countryCode: String): String {
            if (countryCode.length != 2) return "🌐"
            val firstChar = Character.codePointAt(countryCode.uppercase(), 0) - 0x41 + 0x1F1E6
            val secondChar = Character.codePointAt(countryCode.uppercase(), 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        }

        val ALL_COUNTRIES = listOf(
            Country("bd", "Bangladesh", "🇧🇩"),
            Country("us", "United States", "🇺🇸"),
            Country("in", "India", "🇮🇳"),
            Country("gb", "United Kingdom", "🇬🇧"),
            Country("ca", "Canada", "🇨🇦"),
            Country("de", "Germany", "🇩🇪"),
            Country("es", "Spain", "🇪🇸"),
            Country("fr", "France", "🇫🇷"),
            Country("it", "Italy", "🇮🇹"),
            Country("ru", "Russia", "🇷🇺"),
            Country("br", "Brazil", "🇧🇷"),
            Country("tr", "Turkey", "🇹🇷"),
            Country("pk", "Pakistan", "🇵🇰"),
            Country("sa", "Saudi Arabia", "🇸🇦"),
            Country("ae", "United Arab Emirates", "🇦🇪"),
            Country("qa", "Qatar", "🇶🇦"),
            Country("my", "Malaysia", "🇲🇾"),
            Country("id", "Indonesia", "🇮🇩"),
            Country("sg", "Singapore", "🇸🇬"),
            Country("jp", "Japan", "🇯🇵"),
            Country("kr", "South Korea", "🇰🇷"),
            Country("cn", "China", "🇨🇳"),
            Country("au", "Australia", "🇦🇺"),
            Country("nz", "New Zealand", "🇳🇿"),
            Country("ua", "Ukraine", "🇺🇦"),
            Country("th", "Thailand", "🇹🇭"),
            Country("vn", "Vietnam", "🇻🇳"),
            Country("ph", "Philippines", "🇵🇭"),
            Country("af", "Afghanistan", "🇦🇫"),
            Country("al", "Albania", "🇦🇱"),
            Country("ad", "Andorra", "🇦🇩"),
            Country("ar", "Argentina", "🇦🇷"),
            Country("am", "Armenia", "🇦🇲"),
            Country("at", "Austria", "🇦🇹"),
            Country("az", "Azerbaijan", "🇦🇿"),
            Country("bh", "Bahrain", "🇧🇭"),
            Country("be", "Belgium", "🇧🇪"),
            Country("bt", "Bhutan", "🇧🇹"),
            Country("gh", "Ghana", "🇬🇭"),
            Country("mv", "Maldives", "🇲🇻"),
            Country("np", "Nepal", "🇳🇵"),
            Country("nl", "Netherlands", "🇳🇱"),
            Country("ng", "Nigeria", "🇳🇬"),
            Country("pt", "Portugal", "🇵🇹"),
            Country("lk", "Sri Lanka", "🇱🇰")
        )
    }
}
