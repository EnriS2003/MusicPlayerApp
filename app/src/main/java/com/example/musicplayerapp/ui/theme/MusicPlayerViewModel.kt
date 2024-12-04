package com.example.musicplayerapp.ui.theme

import androidx.lifecycle.ViewModel
import com.example.musicplayerapp.R
import com.example.musicplayerapp.model.Album
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayerViewModel : ViewModel() {
    private val albums = listOf(

        Album(
            id = R.drawable.adele,
            songName = "Cry Your Heart Out",
            artistName = "Adele",
            releaseMonth = "November",
            releaseYear = 2021,
            trackCount = 12,
            description = "An emotional journey with powerful vocals."
        ),
        Album(
            id = R.drawable.amy,
            songName = "Tears Dry On Their Own",
            artistName = "Amy Winehouse",
            releaseMonth = "July",
            releaseYear = 2006,
            trackCount = 10,
            description = "A soulful masterpiece by Amy Winehouse."
        ),
        Album(
            id = R.drawable.bruce,
            songName = "Born In The U.S.A.",
            artistName = "Bruce Springsteen",
            releaseMonth = "June",
            releaseYear = 1984,
            trackCount = 8,
            description = "Classic rock from the iconic Bruce Springsteen."
        ),
        Album(
            id = R.drawable.elvis1,
            songName = "Blue Suede Shoes",
            artistName = "Elvis Presley",
            releaseMonth = "March",
            releaseYear = 1956,
            trackCount = 14,
            description = "A rock 'n' roll legend's timeless hits."
        )
    )

    private val _uiState = MutableStateFlow(albums.first())
    val uiState: StateFlow<Album> = _uiState.asStateFlow()

    fun randomizeAlbum() {
        _uiState.value = albums.random()
    }
}