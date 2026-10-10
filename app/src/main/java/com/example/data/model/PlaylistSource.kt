package com.example.data.model

data class PlaylistSource(
    val id: String,
    val name: String,
    val url: String,
    val description: String,
    val isEnabled: Boolean = true,
    val isCustom: Boolean = false
) {
    companion object {
        val DEFAULT_PRESETS = listOf(
            PlaylistSource(
                id = "free_tv",
                name = "Free-TV World",
                url = "https://raw.githubusercontent.com/Free-TV/IPTV/master/playlist.m3u8",
                description = "Global public television channels"
            ),
            PlaylistSource(
                id = "sports_tv",
                name = "Sports TV",
                url = "https://iptv-org.github.io/iptv/categories/sports.m3u",
                description = "International sports and matches"
            ),
            PlaylistSource(
                id = "samsung_tv",
                name = "Samsung TV Plus",
                url = "https://apsattv.com/ssungusa.m3u",
                description = "Free streaming TV channels"
            ),
            PlaylistSource(
                id = "xumo_tv",
                name = "Xumo TV",
                url = "https://www.apsattv.com/xumo.m3u",
                description = "Xumo streaming linear channels"
            ),
            PlaylistSource(
                id = "xiaomi_tv",
                name = "Xiaomi TV",
                url = "https://www.apsattv.com/xiaomi.m3u",
                description = "Xiaomi TV live channels"
            ),
            PlaylistSource(
                id = "lg_channels",
                name = "LG Channels",
                url = "https://www.apsattv.com/lg.m3u",
                description = "LG Channels streaming"
            ),
            PlaylistSource(
                id = "bd_iptv",
                name = "Bangladesh IPTV",
                url = "https://raw.githubusercontent.com/Shadmanislam/bdiptv/master/BD%20IPTV.m3u",
                description = "Bangla entertainment, news & live TV"
            ),
            PlaylistSource(
                id = "bd_channels_1",
                name = "Bangladesh All Channels",
                url = "https://iptv-org.github.io/iptv/countries/bd.m3u",
                description = "Official Bangladesh channels list"
            ),
            PlaylistSource(
                id = "top_movies",
                name = "Top Movies",
                url = "https://aymrgknetzpucldhpkwm.supabase.co/storage/v1/object/public/tmdb/top-movies.m3u",
                description = "Featured on-demand movies"
            ),
            PlaylistSource(
                id = "action_movies",
                name = "Action Movies",
                url = "https://aymrgknetzpucldhpkwm.supabase.co/storage/v1/object/public/tmdb/action-movies.m3u",
                description = "High adrenaline & action movies"
            ),
            PlaylistSource(
                id = "horror_movies",
                name = "Horror Movies",
                url = "https://aymrgknetzpucldhpkwm.supabase.co/storage/v1/object/public/tmdb/horror-movies.m3u",
                description = "Thrills and horror films"
            ),
            PlaylistSource(
                id = "drama_movies",
                name = "Drama Movies",
                url = "https://aymrgknetzpucldhpkwm.supabase.co/storage/v1/object/public/tmdb/drama-movies.m3u",
                description = "Critically acclaimed drama"
            ),
            PlaylistSource(
                id = "adventure_movies",
                name = "Adventure Movies",
                url = "https://aymrgknetzpucldhpkwm.supabase.co/storage/v1/object/public/tmdb/adventure-movies.m3u",
                description = "Epic journeys and adventure"
            ),
            PlaylistSource(
                id = "trending_series",
                name = "Trending Series",
                url = "https://aymrgknetzpucldhpkwm.supabase.co/storage/v1/object/public/tmdb/trending-series.m3u",
                description = "Popular binge-worthy TV series"
            ),
            PlaylistSource(
                id = "iptv_index",
                name = "IPTV-Org Global Index",
                url = "https://iptv-org.github.io/iptv/index.m3u",
                description = "Massive global community directory",
                isEnabled = false // Disabled by default for speed, toggleable in Settings
            )
        )
    }
}
