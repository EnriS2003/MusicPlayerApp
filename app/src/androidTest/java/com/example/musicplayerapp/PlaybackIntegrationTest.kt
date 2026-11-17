package com.example.musicplayerapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayerapp.data.Song
import com.example.musicplayerapp.playback.MusicPlaybackService
import com.example.musicplayerapp.playback.PlaybackController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicReference

class PlaybackIntegrationTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    private fun <T> onMain(block: () -> T): T {
        val result = AtomicReference<T>()
        instrumentation.runOnMainSync { result.set(block()) }
        return result.get()
    }

    @Test fun realPlaybackKeepsQueueSelectionAndSurvivesBackgrounding() {
        val context = instrumentation.targetContext
        val wav = createSilentWav(context)
        val songs = listOf("a", "b", "c").map { id ->
            Song(id, "Test $id / # ?", "Test", audioUri = Uri.fromFile(wav).toString())
        }
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        var playback = onMain { PlaybackController(context, scope).also { it.setSongs(songs) } }
        try {
            compose.waitUntil(15_000) { playback.state.value.connected && playback.state.value.durationMs > 0 }
            onMain { playback.select("b") }
            compose.waitUntil(10_000) { playback.state.value.songId == "b" && playback.state.value.positionMs > 300 }
            onMain { playback.toggleShuffle(); playback.next() }
            compose.waitUntil(5_000) { playback.state.value.songId != "b" }
            val shuffledId = playback.state.value.songId
            onMain { playback.toggleShuffle(); playback.next() }
            val expected = songs[(songs.indexOfFirst { it.id == shuffledId } + 1) % songs.size].id
            compose.waitUntil(5_000) { playback.state.value.songId == expected }
            onMain { playback.previous() }
            compose.waitUntil(5_000) { playback.state.value.songId == shuffledId }
            onMain { playback.seekTo(5_000) }
            compose.waitUntil(5_000) { playback.state.value.positionMs >= 5_000 }
            val beforeAppend = playback.state.value.positionMs
            onMain { playback.setSongs(songs + songs.first().copy(id = "d")) }
            assertEquals(shuffledId, playback.state.value.songId)
            assertTrue(playback.state.value.positionMs >= beforeAppend - 300)
            val beforeReconnect = playback.state.value.positionMs
            onMain {
                playback.release()
                playback = PlaybackController(context, scope).also {
                    it.setSongs(songs + songs.first().copy(id = "d"))
                }
            }
            compose.waitUntil(5_000) { playback.state.value.connected && playback.state.value.songId == shuffledId }
            assertTrue(playback.state.value.positionMs >= beforeReconnect - 300)
            compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
            val backgroundPosition = playback.state.value.positionMs
            compose.waitUntil(5_000) { playback.state.value.positionMs > backgroundPosition + 300 }
            assertTrue(playback.state.value.isPlaying)
            compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
            onMain { playback.togglePlay() }
            compose.waitUntil(5_000) { !playback.state.value.isPlaying }
            onMain { playback.setSongs(songs.filterNot { it.id == shuffledId }) }
            compose.waitUntil(5_000) { playback.state.value.songId != shuffledId }
            assertTrue(playback.state.value.positionMs < 1_000)
            onMain { playback.setSongs(emptyList()) }
            compose.waitUntil(5_000) { playback.state.value.songId == null }
            assertEquals(false, playback.state.value.isPlaying)
            onMain { playback.setSongs(songs) }
            compose.waitUntil(5_000) { playback.state.value.durationMs > 0 }
            assertEquals(false, playback.state.value.isPlaying)
        } finally {
            onMain { playback.release(); scope.cancel() }
            context.stopService(Intent(context, MusicPlaybackService::class.java))
            wav.delete()
        }
    }

    /** A silent, original fixture: exercises decoding without shipping copyrighted music. */
    private fun createSilentWav(context: Context): File {
        val samples = 16_000 * 20
        val bytes = samples * 2
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
            .put("RIFF".toByteArray()).putInt(36 + bytes).put("WAVEfmt ".toByteArray())
            .putInt(16).putShort(1).putShort(1).putInt(16_000).putInt(32_000)
            .putShort(2).putShort(16).put("data".toByteArray()).putInt(bytes).array()
        return File(context.cacheDir, "playback-test.wav").apply {
            outputStream().use { it.write(header); it.write(ByteArray(bytes)) }
        }
    }
}
