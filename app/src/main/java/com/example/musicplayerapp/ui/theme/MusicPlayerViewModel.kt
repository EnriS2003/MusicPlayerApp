package com.example.musicplayerapp.ui.theme

import androidx.lifecycle.ViewModel
import com.example.musicplayerapp.R
import com.example.musicplayerapp.model.Album
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MusicUiState(
    val currentAlbum: Int = R.drawable.adele,
    val albumText: String = "Adele - Cry Your Heart Out"
)

class MusicPlayerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

//    private val albums = listOf(
//        Album(
//            id = R.drawable.adele,
//            name = "Adele - Cry Your Heart Out",
//            releaseMonth = "November",
//            releaseYear = 2021,
//            trackCount = 12,
//            description = "An emotional journey with powerful vocals, describing part of the life of madame Adele."
//        ),
//        Album(
//            id = R.drawable.amy,
//            name = "Amy Winehouse - Tears Dry On Their Own",
//            releaseMonth = "July",
//            releaseYear = 2006,
//            trackCount = 10,
//            description = "A soulful masterpiece by Amy Winehouse."
//        ),
//        Album(
//            id = R.drawable.bruce,
//            name = "Bruce Springsteen - Born In The U.S.A.",
//            releaseMonth = "June",
//            releaseYear = 1984,
//            trackCount = 8,
//            description = "Classic rock from the iconic Bruce Springsteen."
//        ),
//        Album(
//            id = R.drawable.elvis1,
//            name = "Elvis Presley - Blue Suede Shoes",
//            releaseMonth = "March",
//            releaseYear = 1956,
//            trackCount = 14,
//            description = "A rock 'n' roll legend's timeless hits, made by the King of Rock."
//        )
//    )

    // Album list and descriptions
    private val albums = listOf(
        Pair(R.drawable.adele, "Adele - Cry Your Heart Out"),
        Pair(R.drawable.amy, "Amy Winehouse - Tears Dry On Their Own"),
        Pair(R.drawable.bruce, "Bruce Springsteen - Born In The U.S.A."),
        Pair(R.drawable.elvis1, "Elvis Presley - Blue Suede Shoes"),
        Pair(R.drawable.citt_incantata, "Joe Hisaishi - The Last Station")
    )

    fun randomizeAlbum() {
        val newAlbum = albums.random()
        _uiState.value = MusicUiState(
            currentAlbum = newAlbum.first,
            albumText = newAlbum.second
        )
    }
}