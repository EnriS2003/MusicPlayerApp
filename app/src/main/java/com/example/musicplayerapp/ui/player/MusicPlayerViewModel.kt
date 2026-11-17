package com.example.musicplayerapp.ui.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.ImportResult
import com.example.musicplayerapp.data.MusicLibrary
import com.example.musicplayerapp.data.PreviewCatalog
import com.example.musicplayerapp.data.Song
import com.example.musicplayerapp.playback.PlaybackController
import com.example.musicplayerapp.playback.PlaybackState
import com.example.musicplayerapp.playback.PreviewQueue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlayerUiState(
    val songs: List<Song> = PreviewCatalog.songs,
    val currentSong: Song = PreviewCatalog.songs.first(),
    val playback: PlaybackState = PlaybackState(),
    val importing: Boolean = false,
    val importResult: ImportResult? = null
) {
    val isPreview: Boolean get() = !currentSong.isPlayable
}

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val library = MusicLibrary(application)
    private val playback = PlaybackController(application, viewModelScope)
    private val previewQueue = PreviewQueue(PreviewCatalog.songs.map { it.id })
    private val previewId = MutableStateFlow(previewQueue.currentId)
    private val importing = MutableStateFlow(false)
    private val importResult = MutableStateFlow<ImportResult?>(null)

    val uiState = combine(library.songs, playback.state, previewId, importing, importResult) {
        songs, state, preview, loading, result ->
        val catalogue = songs.ifEmpty { PreviewCatalog.songs }
        PlayerUiState(
            songs = catalogue,
            currentSong = catalogue.find { it.id == if (songs.isEmpty()) preview else state.songId }
                ?: catalogue.first(),
            playback = if (songs.isEmpty()) PlaybackState() else state,
            importing = loading, importResult = result
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerUiState())

    init {
        viewModelScope.launch { library.songs.collect { playback.setSongs(it) } }
    }

    fun importAudio(uris: List<Uri>) {
        if (uris.isEmpty() || importing.value) return
        importing.value = true
        viewModelScope.launch {
            try {
                importResult.value = library.importAudio(uris)
            } finally {
                importing.value = false
            }
        }
    }
    fun clearImportResult() { importResult.value = null }
    fun removeSong(id: String) { viewModelScope.launch { library.removeSong(id) } }
    fun selectSong(id: String) {
        if (library.songs.value.isEmpty()) previewId.value = previewQueue.select(id)
        else playback.select(id)
    }
    fun next() {
        if (library.songs.value.isEmpty()) previewId.value = previewQueue.next() else playback.next()
    }
    fun previous() {
        if (library.songs.value.isEmpty()) previewId.value = previewQueue.previous() else playback.previous()
    }
    fun shuffle() {
        if (library.songs.value.isEmpty()) previewId.value = previewQueue.randomize()
        else playback.toggleShuffle()
    }
    fun togglePlay() = playback.togglePlay()
    fun seekTo(positionMs: Long) = playback.seekTo(positionMs)
    fun cycleRepeat() = playback.cycleRepeat()

    override fun onCleared() {
        playback.release()
        super.onCleared()
    }
}
