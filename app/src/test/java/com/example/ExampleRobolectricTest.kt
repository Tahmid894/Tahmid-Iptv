package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Category
import com.example.data.model.Country
import com.example.data.parser.M3UParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Tahmid IPTV", appName)
  }

  @Test
  fun `verify category list contains 22 categories`() {
    assertEquals(22, Category.ALL_CATEGORIES.size)
  }

  @Test
  fun `verify m3u parser parses channels and metadata accurately`() {
    val sampleM3u = """
      #EXTM3U
      #EXTINF:-1 tvg-id="CNN.us" tvg-name="CNN HD" tvg-logo="https://example.com/cnn.png" group-title="News",CNN News (1080p)
      https://example.com/cnn/live.m3u8
      #EXTINF:-1 tvg-id="BBC.uk" tvg-name="BBC One" tvg-logo="https://example.com/bbc.png" group-title="General",BBC One 4K
      https://example.com/bbc/live.m3u8
    """.trimIndent()

    val channels = M3UParser.parse(sampleM3u.byteInputStream(), "TestPlaylist")
    assertEquals(2, channels.size)

    val first = channels[0]
    assertEquals("CNN News (1080p)", first.name)
    assertEquals("CNN.us", first.tvgId)
    assertEquals("News", first.groupTitle)
    assertEquals("https://example.com/cnn.png", first.logoUrl)
    assertEquals("FHD", first.displayQuality)
    assertEquals("https://example.com/cnn/live.m3u8", first.streamUrl)

    val second = channels[1]
    assertEquals("4K", second.displayQuality)
  }
}
