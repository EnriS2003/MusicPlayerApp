package com.example.musicplayerapp.data

import com.example.musicplayerapp.R

/** Artwork-only examples from the original prototype; never presented as playable audio. */
object PreviewCatalog {
    val songs = listOf(
        Song("preview-adele", "Cry Your Heart Out", "Adele", "30", "2021", artworkRes = R.drawable.adele),
        Song("preview-amy", "Tears Dry On Their Own", "Amy Winehouse", "Back to Black", "2006", artworkRes = R.drawable.amy),
        Song("preview-bruce", "Born in the U.S.A.", "Bruce Springsteen", "Born in the U.S.A.", "1984", artworkRes = R.drawable.bruce),
        Song("preview-elvis", "Blue Suede Shoes", "Elvis Presley", "Elvis Presley", "1956", artworkRes = R.drawable.elvis1)
    )
}
