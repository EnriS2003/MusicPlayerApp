package com.example.musicplayerapp.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayerapp.data.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaybackState(
    val songId: String? = null,
    val connected: Boolean = false,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val shuffle: Boolean = false,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
    val repeatMode: Int = Player.REPEAT_MODE_ALL,
    val error: Boolean = false
)

/** The service's player is the single source of truth for selection and transport state. */
class PlaybackController(context: Context, scope: CoroutineScope) {
    private val _state = MutableStateFlow(PlaybackState())
    val state = _state.asStateFlow()
    private var songs: List<Song> = emptyList()
    private var controller: MediaController? = null
    private val future = MediaController.Builder(
        context, SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
    ).buildAsync()
    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) { updateState() }
    }
    private val progressJob: Job

    init {
        future.addListener({
            runCatching { future.get() }.onSuccess { player ->
                controller = player
                player.addListener(listener)
                syncQueue()
                updateState()
            }.onFailure { _state.value = _state.value.copy(error = true) }
        }, ContextCompat.getMainExecutor(context))
        progressJob = scope.launch {
            while (true) {
                updateState()
                delay(300)
            }
        }
    }

    fun setSongs(value: List<Song>) {
        songs = value.filter { it.isPlayable }
        syncQueue()
    }
    private fun syncQueue() {
        val player = controller ?: return
        val existing = (0 until player.mediaItemCount).map { player.getMediaItemAt(it).mediaId }
        val desired = songs.map { it.id }
        if (existing == desired) return // Reattach to background playback without restarting it.
        if (desired.isEmpty()) {
            player.pause()
            player.clearMediaItems()
            updateState()
            return
        }
        if (existing.isNotEmpty() && desired.take(existing.size) == existing) {
            player.addMediaItems(songs.drop(existing.size).map { it.toMediaItem() })
        } else {
            val retainedIndex = desired.indexOf(player.currentMediaItem?.mediaId)
            val index = if (retainedIndex >= 0) retainedIndex
                else player.currentMediaItemIndex.coerceIn(0, desired.lastIndex)
            val position = if (retainedIndex >= 0) player.currentPosition else 0L
            player.setMediaItems(songs.map { it.toMediaItem() }, index, position)
        }
        if (songs.isNotEmpty() && player.playbackState == Player.STATE_IDLE) player.prepare()
        updateState()
    }
    fun select(id: String, play: Boolean = true) {
        val player = controller ?: return
        val index = songs.indexOfFirst { it.id == id }
        if (index < 0) return
        player.seekToDefaultPosition(index)
        player.prepare()
        if (play) player.play()
    }
    fun togglePlay() {
        val player = controller ?: return
        if (player.playWhenReady && player.playbackState != Player.STATE_ENDED && player.playerError == null) player.pause()
        else {
            if (player.playbackState == Player.STATE_ENDED) player.seekToDefaultPosition()
            player.prepare()
            player.play()
        }
    }
    fun next() {
        controller?.let {
            it.seekToNextMediaItem()
            if (it.playbackState == Player.STATE_IDLE) it.prepare()
        }
    }
    fun previous() {
        controller?.let {
            it.seekToPreviousMediaItem()
            if (it.playbackState == Player.STATE_IDLE) it.prepare()
        }
    }
    fun seekTo(positionMs: Long) { controller?.seekTo(positionMs.coerceAtLeast(0)) }
    fun toggleShuffle() { controller?.let { it.shuffleModeEnabled = !it.shuffleModeEnabled } }
    fun cycleRepeat() {
        controller?.let {
            it.repeatMode = when (it.repeatMode) {
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                else -> Player.REPEAT_MODE_ALL
            }
        }
    }
    private fun updateState() {
        val player = controller ?: return
        if (!player.isConnected) {
            _state.value = _state.value.copy(connected = false, isPlaying = false, error = true)
            return
        }
        _state.value = PlaybackState(
            songId = player.currentMediaItem?.mediaId, connected = player.isConnected,
            isPlaying = player.playWhenReady && player.playbackState != Player.STATE_ENDED && player.playerError == null,
            isBuffering = player.playbackState == Player.STATE_BUFFERING,
            positionMs = player.currentPosition.coerceAtLeast(0),
            durationMs = player.duration.takeUnless { it == C.TIME_UNSET }?.coerceAtLeast(0) ?: 0,
            shuffle = player.shuffleModeEnabled, repeatMode = player.repeatMode,
            hasNext = player.hasNextMediaItem(), hasPrevious = player.hasPreviousMediaItem(),
            error = player.playerError != null
        )
    }
    fun release() {
        progressJob.cancel()
        controller?.removeListener(listener)
        MediaController.releaseFuture(future)
        controller = null
    }
}

fun Song.toMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(id).setUri(requireNotNull(audioUri))
    .setMediaMetadata(MediaMetadata.Builder()
        .setTitle(title).setArtist(artist).setAlbumTitle(album)
        .setArtworkUri(artworkUri?.let(Uri::parse)).build())
    .build()
