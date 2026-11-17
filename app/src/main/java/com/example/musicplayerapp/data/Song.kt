package com.example.musicplayerapp.data

/** An ID is stable across launches and independent of artwork or display text. */
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val year: String = "",
    val audioUri: String? = null,
    val artworkUri: String? = null,
    val artworkRes: Int? = null,
    val durationMs: Long = 0L
) {
    val isPlayable: Boolean get() = audioUri != null
}
