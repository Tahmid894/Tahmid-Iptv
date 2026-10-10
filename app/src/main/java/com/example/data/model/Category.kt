package com.example.data.model

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val directPlaylistUrl: String? = null,
    val isSpecial: Boolean = false // e.g. Countries, Languages, Favorites
) {
    companion object {
        val ALL_CATEGORIES = listOf(
            Category(
                id = "live_tv",
                name = "Live TV",
                description = "General and global broadcast channels",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/general.m3u"
            ),
            Category(
                id = "movies",
                name = "Movies",
                description = "Feature films, blockbusters & classics",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/movies.m3u"
            ),
            Category(
                id = "series",
                name = "Series",
                description = "TV series, drama shows & episodes",
                directPlaylistUrl = "https://aymrgknetzpucldhpkwm.supabase.co/storage/v1/object/public/tmdb/trending-series.m3u"
            ),
            Category(
                id = "animation",
                name = "Animation",
                description = "Anime, cartoons & animated features",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/animation.m3u"
            ),
            Category(
                id = "music",
                name = "Music",
                description = "Music videos, concerts & radio TV",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/music.m3u"
            ),
            Category(
                id = "auto",
                name = "Auto",
                description = "Motorsports, cars & auto reviews",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/auto.m3u"
            ),
            Category(
                id = "sports",
                name = "Sports",
                description = "Live games, football, cricket & highlights",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/sports.m3u"
            ),
            Category(
                id = "news",
                name = "News",
                description = "Breaking news, world affairs & weather",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/news.m3u"
            ),
            Category(
                id = "cooking",
                name = "Cooking",
                description = "Culinary arts, food recipes & chefs",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/cooking.m3u"
            ),
            Category(
                id = "kids",
                name = "Kids",
                description = "Cartoons, children stories & learning",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/kids.m3u"
            ),
            Category(
                id = "education",
                name = "Education",
                description = "Documentaries, lectures & knowledge",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/education.m3u"
            ),
            Category(
                id = "business",
                name = "Business",
                description = "Finance, stocks, markets & economy",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/business.m3u"
            ),
            Category(
                id = "relax",
                name = "Relax",
                description = "Meditation, ambient nature & lo-fi",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/relax.m3u"
            ),
            Category(
                id = "entertainment",
                name = "Entertainment",
                description = "Talk shows, reality TV & variety",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/entertainment.m3u"
            ),
            Category(
                id = "lifestyle",
                name = "Lifestyle",
                description = "Travel, fashion, homes & design",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/lifestyle.m3u"
            ),
            Category(
                id = "science",
                name = "Science",
                description = "Space, technology, nature & discovery",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/science.m3u"
            ),
            Category(
                id = "comedy",
                name = "Comedy",
                description = "Stand-up comedy, sitcoms & laughter",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/comedy.m3u"
            ),
            Category(
                id = "family",
                name = "Family",
                description = "Family-friendly entertainment for all",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/family.m3u"
            ),
            Category(
                id = "shop",
                name = "Shop",
                description = "Home shopping & product showcases",
                directPlaylistUrl = "https://iptv-org.github.io/iptv/categories/shop.m3u"
            ),
            Category(
                id = "countries",
                name = "Countries",
                description = "Explore channels by country flags",
                isSpecial = true
            ),
            Category(
                id = "languages",
                name = "Languages",
                description = "Explore channels by spoken language",
                isSpecial = true
            ),
            Category(
                id = "favorites",
                name = "Favorites",
                description = "Your saved favorite channels",
                isSpecial = true
            )
        )
    }
}
