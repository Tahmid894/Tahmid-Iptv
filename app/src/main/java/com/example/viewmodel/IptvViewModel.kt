package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PreferencesManager
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.data.model.PlaylistSource
import com.example.data.repository.PlaylistRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ChannelsUiState {
    object Idle : ChannelsUiState()
    data class Loading(val message: String = "Loading playlist...") : ChannelsUiState()
    data class Success(val channels: List<Channel>, val playlistUrl: String?) : ChannelsUiState()
    data class Error(val message: String, val canRetry: Boolean = true) : ChannelsUiState()
}

class IptvViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlaylistRepository(application)
    private val preferencesManager = PreferencesManager(application)

    private val _navigationStack = mutableListOf<Screen>(Screen.Home)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _channelsState = MutableStateFlow<ChannelsUiState>(ChannelsUiState.Idle)
    val channelsState: StateFlow<ChannelsUiState> = _channelsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _cacheSize = MutableStateFlow(repository.getCacheSizeDescription())
    val cacheSize: StateFlow<String> = _cacheSize.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = preferencesManager.favoriteChannelIdsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val recentChannels: StateFlow<List<Channel>> = preferencesManager.recentChannelsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customPlaylists: StateFlow<List<PlaylistSource>> = preferencesManager.customPlaylistsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val disabledPresets: StateFlow<Set<String>> = preferencesManager.disabledPresetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            _navigationStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_navigationStack.size > 1) {
            _navigationStack.removeAt(_navigationStack.lastIndex)
            _currentScreen.value = _navigationStack.last()
            return true
        }
        return false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadCategory(category: Category, forceRefresh: Boolean = false) {
        if (category.isSpecial) {
            when (category.id) {
                "countries" -> navigateTo(Screen.Countries)
                "languages" -> navigateTo(Screen.Languages)
                "favorites" -> loadFavorites()
            }
            return
        }

        val url = category.directPlaylistUrl
            ?: "https://iptv-org.github.io/iptv/categories/${category.id}.m3u"
        navigateTo(
            Screen.ChannelList(
                title = category.name,
                subtitle = category.description,
                playlistUrl = url
            )
        )
        fetchChannels(url, category.name, forceRefresh)
    }

    fun loadCountry(country: Country, forceRefresh: Boolean = false) {
        val url = country.playlistUrl
        navigateTo(
            Screen.ChannelList(
                title = "${country.flagEmoji} ${country.name}",
                subtitle = "Country Code: ${country.code.uppercase()}",
                playlistUrl = url
            )
        )
        fetchChannels(url, country.name, forceRefresh)
    }

    fun loadLanguage(language: Language, forceRefresh: Boolean = false) {
        val url = language.playlistUrl
        navigateTo(
            Screen.ChannelList(
                title = "${language.name} (${language.nativeName})",
                subtitle = "Language: ${language.code}",
                playlistUrl = url
            )
        )
        fetchChannels(url, language.name, forceRefresh)
    }

    fun loadCustomOrPresetSource(source: PlaylistSource, forceRefresh: Boolean = false) {
        navigateTo(
            Screen.ChannelList(
                title = source.name,
                subtitle = source.description,
                playlistUrl = source.url
            )
        )
        fetchChannels(source.url, source.name, forceRefresh)
    }

    fun loadFavorites() {
        navigateTo(
            Screen.ChannelList(
                title = "Favorites",
                subtitle = "Saved Channels",
                isFavorites = true
            )
        )
        refreshFavoritesList()
    }

    fun refreshFavoritesList() {
        val faves = favoriteIds.value
        val allCached = mutableListOf<Channel>()
        // Look in recent channels first
        recentChannels.value.forEach { ch ->
            if (faves.contains(ch.id)) allCached.add(ch.copy(isFavorite = true))
        }
        _channelsState.value = ChannelsUiState.Success(allCached.distinctBy { it.id }, null)
    }

    fun loadRecentlyPlayed() {
        navigateTo(
            Screen.ChannelList(
                title = "Recently Played",
                subtitle = "History",
                isRecentlyPlayed = true
            )
        )
        val recents = recentChannels.value.map { ch ->
            ch.copy(isFavorite = favoriteIds.value.contains(ch.id))
        }
        _channelsState.value = ChannelsUiState.Success(recents, null)
    }

    fun reloadCurrentChannels() {
        val screen = _currentScreen.value
        if (screen is Screen.ChannelList) {
            when {
                screen.isFavorites -> refreshFavoritesList()
                screen.isRecentlyPlayed -> loadRecentlyPlayed()
                screen.playlistUrl != null -> fetchChannels(screen.playlistUrl, screen.title, forceRefresh = true)
            }
        }
    }

    private fun fetchChannels(url: String, sourceName: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _channelsState.value = ChannelsUiState.Loading("Loading $sourceName channels...")
            val result = repository.fetchPlaylist(url, sourceName, forceRefresh)
            result.fold(
                onSuccess = { channels ->
                    val faves = favoriteIds.value
                    val mapped = channels.map { it.copy(isFavorite = faves.contains(it.id)) }
                    _channelsState.value = ChannelsUiState.Success(mapped, url)
                },
                onFailure = { error ->
                    _channelsState.value = ChannelsUiState.Error(
                        error.localizedMessage ?: "Unable to connect or download playlist."
                    )
                }
            )
        }
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            preferencesManager.toggleFavorite(channel.id)
            // Update current success state if present
            val current = _channelsState.value
            if (current is ChannelsUiState.Success) {
                val updated = current.channels.map {
                    if (it.id == channel.id) it.copy(isFavorite = !it.isFavorite) else it
                }
                _channelsState.value = current.copy(channels = updated)
            }
        }
    }

    fun onChannelPlayed(channel: Channel) {
        viewModelScope.launch {
            preferencesManager.addRecentChannel(channel)
        }
    }

    fun addCustomPlaylist(name: String, url: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            if (name.isBlank() || url.isBlank()) {
                onResult(false, "Name and URL cannot be empty")
                return@launch
            }
            if (!url.startsWith("http://", ignoreCase = true) && !url.startsWith("https://", ignoreCase = true)) {
                onResult(false, "URL must start with http:// or https://")
                return@launch
            }
            val validation = repository.validatePlaylistUrl(url)
            validation.fold(
                onSuccess = { count ->
                    preferencesManager.addCustomPlaylist(name.trim(), url.trim())
                    onResult(true, "Found $count channels! Added successfully.")
                },
                onFailure = { err ->
                    // Still allow adding if user insists or server blocks HEAD/GET with custom agent, but warn
                    preferencesManager.addCustomPlaylist(name.trim(), url.trim())
                    onResult(true, "Added! Note: server responded with ${err.localizedMessage}")
                }
            )
        }
    }

    fun removeCustomPlaylist(id: String) {
        viewModelScope.launch {
            preferencesManager.removeCustomPlaylist(id)
        }
    }

    fun togglePreset(presetId: String, enable: Boolean) {
        viewModelScope.launch {
            preferencesManager.setPresetEnabled(presetId, enable)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _cacheSize.value = repository.getCacheSizeDescription()
        }
    }
}
