package com.example.data.repository

import android.content.Context
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.data.model.PlaylistSource
import com.example.data.parser.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PlaylistRepository(private val context: Context) {

    private val cacheDir = File(context.cacheDir, "iptv_http_cache").apply { mkdirs() }
    private val diskCacheDir = File(context.cacheDir, "iptv_playlists").apply { mkdirs() }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(Cache(cacheDir, 50L * 1024L * 1024L)) // 50MB disk cache
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    // In-memory cache by URL to make screen switches ultra fast
    private val memoryCache = ConcurrentHashMap<String, List<Channel>>()

    suspend fun fetchPlaylist(
        url: String,
        sourceName: String? = null,
        forceRefresh: Boolean = false
    ): Result<List<Channel>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            memoryCache[url]?.let {
                if (it.isNotEmpty()) return@withContext Result.success(it)
            }
        }

        val cacheFile = File(diskCacheDir, url.hashCode().toString() + ".m3u")

        // If not force refreshing and cache exists and is newer than 4 hours, load from disk cache first
        if (!forceRefresh && cacheFile.exists() && (System.currentTimeMillis() - cacheFile.lastModified() < 4 * 60 * 60 * 1000)) {
            try {
                FileInputStream(cacheFile).use { fis ->
                    val channels = M3UParser.parse(fis, sourceName)
                    if (channels.isNotEmpty()) {
                        memoryCache[url] = channels
                        return@withContext Result.success(channels)
                    }
                }
            } catch (_: Exception) {
                // fall through to network request
            }
        }

        try {
            val requestBuilder = Request.Builder().url(url)
                .header("User-Agent", "TahmidIPTV/1.0 (Android; ExoPlayer)")
                .header("Accept", "*/*")

            if (forceRefresh) {
                requestBuilder.header("Cache-Control", "no-cache")
            }

            val request = requestBuilder.build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                // Try fallback from disk cache if network error
                if (cacheFile.exists()) {
                    FileInputStream(cacheFile).use { fis ->
                        val fallback = M3UParser.parse(fis, sourceName)
                        if (fallback.isNotEmpty()) {
                            memoryCache[url] = fallback
                            return@withContext Result.success(fallback)
                        }
                    }
                }
                return@withContext Result.failure(
                    Exception("Failed to load playlist: HTTP ${response.code} ${response.message}")
                )
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty playlist response"))

            // Stream response to disk cache file while parsing
            val bytes = body.bytes()
            try {
                FileOutputStream(cacheFile).use { fos ->
                    fos.write(bytes)
                }
            } catch (_: Exception) {}

            val channels = bytes.inputStream().use { stream ->
                M3UParser.parse(stream, sourceName)
            }

            if (channels.isEmpty()) {
                return@withContext Result.failure(Exception("No playable channels found in this playlist."))
            }

            memoryCache[url] = channels
            Result.success(channels)
        } catch (e: Exception) {
            // Check disk cache fallback on error
            if (cacheFile.exists()) {
                try {
                    FileInputStream(cacheFile).use { fis ->
                        val fallback = M3UParser.parse(fis, sourceName)
                        if (fallback.isNotEmpty()) {
                            memoryCache[url] = fallback
                            return@withContext Result.success(fallback)
                        }
                    }
                } catch (_: Exception) {}
            }
            Result.failure(e)
        }
    }

    suspend fun fetchCategoryChannels(
        category: Category,
        forceRefresh: Boolean = false
    ): Result<List<Channel>> {
        val targetUrl = category.directPlaylistUrl
            ?: "https://iptv-org.github.io/iptv/categories/${category.id}.m3u"
        return fetchPlaylist(targetUrl, sourceName = category.name, forceRefresh = forceRefresh)
    }

    suspend fun fetchCountryChannels(
        country: Country,
        forceRefresh: Boolean = false
    ): Result<List<Channel>> {
        return fetchPlaylist(country.playlistUrl, sourceName = country.name, forceRefresh = forceRefresh)
    }

    suspend fun fetchLanguageChannels(
        language: Language,
        forceRefresh: Boolean = false
    ): Result<List<Channel>> {
        return fetchPlaylist(language.playlistUrl, sourceName = language.name, forceRefresh = forceRefresh)
    }

    suspend fun validatePlaylistUrl(url: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "TahmidIPTV/1.0 (Android; ExoPlayer)")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Server returned HTTP ${response.code}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty response"))
            val channels = body.byteStream().use { stream ->
                M3UParser.parse(stream, "Custom")
            }

            if (channels.isEmpty()) {
                return@withContext Result.failure(Exception("No channels found. Ensure it is a valid M3U file."))
            }
            Result.success(channels.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearCache() {
        memoryCache.clear()
        try {
            okHttpClient.cache?.evictAll()
            diskCacheDir.listFiles()?.forEach { it.delete() }
        } catch (_: Exception) {}
    }

    fun getCacheSizeDescription(): String {
        var sizeBytes = 0L
        diskCacheDir.listFiles()?.forEach { sizeBytes += it.length() }
        cacheDir.listFiles()?.forEach { sizeBytes += it.length() }
        val sizeMb = sizeBytes.toDouble() / (1024.0 * 1024.0)
        return String.format("%.2f MB", sizeMb)
    }
}
