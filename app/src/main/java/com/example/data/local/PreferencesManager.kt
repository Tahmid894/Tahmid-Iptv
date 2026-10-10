package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.Channel
import com.example.data.model.PlaylistSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tahmid_iptv_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_FAVORITE_IDS = stringSetPreferencesKey("favorite_ids")
        private val KEY_RECENT_CHANNELS = stringPreferencesKey("recent_channels_json")
        private val KEY_CUSTOM_PLAYLISTS = stringPreferencesKey("custom_playlists_json")
        private val KEY_DISABLED_PRESETS = stringSetPreferencesKey("disabled_preset_ids")
    }

    val favoriteChannelIdsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_FAVORITE_IDS] ?: emptySet()
    }

    val disabledPresetsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_DISABLED_PRESETS] ?: emptySet()
    }

    val customPlaylistsFlow: Flow<List<PlaylistSource>> = context.dataStore.data.map { prefs ->
        val jsonStr = prefs[KEY_CUSTOM_PLAYLISTS] ?: return@map emptyList()
        parseCustomPlaylists(jsonStr)
    }

    val recentChannelsFlow: Flow<List<Channel>> = context.dataStore.data.map { prefs ->
        val jsonStr = prefs[KEY_RECENT_CHANNELS] ?: return@map emptyList()
        parseRecentChannels(jsonStr)
    }

    suspend fun toggleFavorite(channelId: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[KEY_FAVORITE_IDS] ?: emptySet()).toMutableSet()
            if (current.contains(channelId)) {
                current.remove(channelId)
            } else {
                current.add(channelId)
            }
            prefs[KEY_FAVORITE_IDS] = current
        }
    }

    suspend fun addRecentChannel(channel: Channel) {
        context.dataStore.edit { prefs ->
            val currentJson = prefs[KEY_RECENT_CHANNELS] ?: "[]"
            val currentList = parseRecentChannels(currentJson).toMutableList()
            // Remove if already exists, then add to front
            currentList.removeAll { it.id == channel.id || it.streamUrl == channel.streamUrl }
            currentList.add(0, channel)
            val trimmed = currentList.take(20)
            prefs[KEY_RECENT_CHANNELS] = serializeRecentChannels(trimmed)
        }
    }

    suspend fun addCustomPlaylist(name: String, url: String) {
        context.dataStore.edit { prefs ->
            val current = parseCustomPlaylists(prefs[KEY_CUSTOM_PLAYLISTS] ?: "[]").toMutableList()
            val newSource = PlaylistSource(
                id = "custom_${System.currentTimeMillis()}",
                name = name,
                url = url,
                description = "User added custom playlist",
                isEnabled = true,
                isCustom = true
            )
            current.add(newSource)
            prefs[KEY_CUSTOM_PLAYLISTS] = serializeCustomPlaylists(current)
        }
    }

    suspend fun removeCustomPlaylist(playlistId: String) {
        context.dataStore.edit { prefs ->
            val current = parseCustomPlaylists(prefs[KEY_CUSTOM_PLAYLISTS] ?: "[]").toMutableList()
            current.removeAll { it.id == playlistId }
            prefs[KEY_CUSTOM_PLAYLISTS] = serializeCustomPlaylists(current)
        }
    }

    suspend fun setPresetEnabled(presetId: String, isEnabled: Boolean) {
        context.dataStore.edit { prefs ->
            val current = (prefs[KEY_DISABLED_PRESETS] ?: emptySet()).toMutableSet()
            if (isEnabled) {
                current.remove(presetId)
            } else {
                current.add(presetId)
            }
            prefs[KEY_DISABLED_PRESETS] = current
        }
    }

    private fun parseRecentChannels(jsonStr: String): List<Channel> {
        val list = mutableListOf<Channel>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Channel(
                        id = obj.optString("id", ""),
                        name = obj.optString("name", "Channel"),
                        streamUrl = obj.optString("streamUrl", ""),
                        logoUrl = obj.optString("logoUrl").takeIf { it.isNotBlank() },
                        groupTitle = obj.optString("groupTitle").takeIf { it.isNotBlank() },
                        quality = obj.optString("quality").takeIf { it.isNotBlank() }
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun serializeRecentChannels(channels: List<Channel>): String {
        val array = JSONArray()
        for (ch in channels) {
            val obj = JSONObject().apply {
                put("id", ch.id)
                put("name", ch.name)
                put("streamUrl", ch.streamUrl)
                ch.logoUrl?.let { put("logoUrl", it) }
                ch.groupTitle?.let { put("groupTitle", it) }
                ch.quality?.let { put("quality", it) }
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseCustomPlaylists(jsonStr: String): List<PlaylistSource> {
        val list = mutableListOf<PlaylistSource>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    PlaylistSource(
                        id = obj.optString("id", ""),
                        name = obj.optString("name", ""),
                        url = obj.optString("url", ""),
                        description = obj.optString("description", "Custom playlist"),
                        isEnabled = obj.optBoolean("isEnabled", true),
                        isCustom = true
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun serializeCustomPlaylists(sources: List<PlaylistSource>): String {
        val array = JSONArray()
        for (src in sources) {
            val obj = JSONObject().apply {
                put("id", src.id)
                put("name", src.name)
                put("url", src.url)
                put("description", src.description)
                put("isEnabled", src.isEnabled)
            }
            array.put(obj)
        }
        return array.toString()
    }
}
