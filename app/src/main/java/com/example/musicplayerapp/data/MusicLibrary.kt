package com.example.musicplayerapp.data

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class ImportResult(val added: Int, val failed: Int)

/** Stores references granted by the document picker, without broad storage permissions. */
class MusicLibrary(private val context: Context) {
    private val preferences = context.getSharedPreferences("music_library", Context.MODE_PRIVATE)
    private val mutex = Mutex()
    private val _songs = MutableStateFlow(readSongs())
    val songs = _songs.asStateFlow()

    suspend fun importAudio(uris: List<Uri>): ImportResult = withContext(Dispatchers.IO) {
        mutex.withLock {
            val imported = _songs.value.toMutableList()
            var added = 0
            var failed = 0
            for (uri in uris.distinct()) {
                if (imported.any { it.audioUri == uri.toString() }) continue
                try {
                    context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    imported += readMetadata(uri)
                    added++
                } catch (_: Exception) {
                    failed++
                }
            }
            if (added > 0) {
                save(imported)
            }
            ImportResult(added, failed)
        }
    }

    suspend fun removeSong(id: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val song = _songs.value.find { it.id == id } ?: return@withLock
            save(_songs.value.filterNot { it.id == id })
            // Remove only our cached artwork and permission; never delete the user's audio file.
            song.artworkUri?.let { uri ->
                runCatching { File(java.net.URI(uri)).delete() }
            }
            song.audioUri?.let { uri ->
                runCatching {
                    context.contentResolver.releasePersistableUriPermission(
                        Uri.parse(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
        }
    }

    private fun save(songs: List<Song>) {
        val json = JSONArray().apply { songs.forEach { put(it.toJson()) } }
        preferences.edit().putString("songs", json.toString()).apply()
        _songs.value = songs.toList()
    }

    private fun readMetadata(uri: Uri): Song {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()?.takeIf { it > 0 } ?: error("Not a readable audio file")
            val id = UUID.randomUUID().toString()
            val filename = runCatching {
                context.contentResolver.query(
                    uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
                )?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
            }.getOrNull()
            val artwork = retriever.embeddedPicture?.let { bytes ->
                File(context.filesDir, "artwork").apply { mkdirs() }
                    .resolve("$id.jpg").apply { writeBytes(bytes) }.toURI().toString()
            }
            return Song(
                id = id,
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ?.takeIf { it.isNotBlank() } ?: filename?.substringBeforeLast('.')
                        ?.takeIf { it.isNotBlank() } ?: "Untitled track",
                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    ?.takeIf { it.isNotBlank() } ?: "Unknown artist",
                album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM).orEmpty(),
                year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR).orEmpty(),
                audioUri = uri.toString(), artworkUri = artwork, durationMs = duration
            )
        } finally {
            retriever.release()
        }
    }

    private fun readSongs(): List<Song> = runCatching {
        val json = JSONArray(preferences.getString("songs", "[]"))
        buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(Song(
                    id = item.getString("id"), title = item.getString("title"),
                    artist = item.getString("artist"), album = item.optString("album"),
                    year = item.optString("year"), audioUri = item.getString("audioUri"),
                    artworkUri = item.optString("artworkUri").takeIf { it.isNotBlank() },
                    durationMs = item.optLong("durationMs")
                ))
            }
        }
    }.getOrDefault(emptyList())

    private fun Song.toJson() = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("artist", artist)
        put("album", album)
        put("year", year)
        put("audioUri", audioUri)
        put("artworkUri", artworkUri.orEmpty())
        put("durationMs", durationMs)
    }
}
