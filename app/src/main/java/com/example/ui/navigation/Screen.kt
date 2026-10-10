package com.example.ui.navigation

import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.data.model.PlaylistSource

sealed class Screen {
    object Home : Screen()
    data class ChannelList(
        val title: String,
        val subtitle: String? = null,
        val playlistUrl: String? = null,
        val isFavorites: Boolean = false,
        val isRecentlyPlayed: Boolean = false
    ) : Screen()
    object Countries : Screen()
    object Languages : Screen()
    data class Player(
        val initialChannel: Channel,
        val playlist: List<Channel> = emptyList()
    ) : Screen()
    object Settings : Screen()
}
