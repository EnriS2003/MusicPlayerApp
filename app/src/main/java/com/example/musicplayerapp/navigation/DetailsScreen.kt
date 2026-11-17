package com.example.musicplayerapp.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.Song
import com.example.musicplayerapp.ui.components.AlbumArtwork
import com.example.musicplayerapp.ui.player.formatTime

@Composable
fun DetailsScreen(song: Song?, onSelect: (String) -> Unit, onRemove: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (song == null) {
            Text(stringResource(R.string.track_not_found))
        } else {
            AlbumArtwork(song, Modifier.widthIn(max = 280.dp).fillMaxWidth().aspectRatio(1f))
            Text(song.title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Text(song.artist, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (song.album.isNotBlank()) Text(stringResource(R.string.album_value, song.album))
            if (song.year.isNotBlank()) Text(stringResource(R.string.year_value, song.year))
            if (song.durationMs > 0) Text(stringResource(R.string.duration_value, formatTime(song.durationMs)))
            Text(stringResource(if (song.isPlayable) R.string.local_file_hint else R.string.preview_hint),
                style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onSelect(song.id) }) {
                Text(stringResource(if (song.isPlayable) R.string.play_track else R.string.show_preview))
            }
            if (song.isPlayable) TextButton(onClick = { onRemove(song.id) }) {
                Text(stringResource(R.string.remove_track), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
