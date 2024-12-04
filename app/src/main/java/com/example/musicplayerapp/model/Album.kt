package com.example.musicplayerapp.model

data class Album(
    val id: Int,             // Resource ID for the album image
    val songName: String,        // Album name
    val artistName: String,       // Artist name
    val releaseMonth: String, // Release month
    val releaseYear: Int,     // Release year
    val trackCount: Int,      // Number of tracks
    val description: String   // Album description
)