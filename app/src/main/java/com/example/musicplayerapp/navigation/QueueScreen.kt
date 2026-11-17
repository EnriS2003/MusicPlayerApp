package com.example.musicplayerapp.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.R
import com.example.musicplayerapp.ui.components.AlbumArtwork
import com.example.musicplayerapp.ui.player.PlayerUiState
import com.example.musicplayerapp.ui.player.formatTime

@Composable
fun QueueScreen(state: PlayerUiState, onSelect: (String) -> Unit, onDetails: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val compact = LocalConfiguration.current.screenHeightDp < 480
    val filtered = state.songs.filter {
        it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
    }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        if (!compact) Text(stringResource(if (state.isPreview) R.string.preview_queue_hint else R.string.queue_hint),
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 16.dp))
        OutlinedTextField(value = query, onValueChange = { query = it },
            label = { Text(stringResource(R.string.search_tracks)) }, singleLine = true,
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) {
                Icon(Icons.Rounded.Close, stringResource(R.string.clear_search))
            } }, modifier = Modifier.fillMaxWidth())
        Text(pluralStringResource(R.plurals.track_count, filtered.size, filtered.size), style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = if (compact) 8.dp else 16.dp))
        if (filtered.isEmpty()) {
            Text(stringResource(R.string.no_results), Modifier.padding(vertical = 24.dp))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered, key = { it.id }) { song ->
                val current = song.id == state.currentSong.id
                Card(colors = CardDefaults.cardColors(containerColor = if (current)
                    MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(Modifier.fillMaxWidth().semantics { selected = current }
                        .clickable(onClickLabel = stringResource(R.string.select_track)) { onSelect(song.id) }
                        .padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AlbumArtwork(song, Modifier.size(56.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(song.title, style = MaterialTheme.typography.titleSmall)
                            Text(song.artist, style = MaterialTheme.typography.bodySmall)
                            if (song.durationMs > 0) Text(formatTime(song.durationMs), style = MaterialTheme.typography.labelSmall)
                        }
                        if (current) Icon(Icons.Rounded.GraphicEq, stringResource(R.string.selected_track),
                            tint = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = { onDetails(song.id) }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowForward, stringResource(R.string.song_details))
                        }
                    }
                }
            }
        }
    }
}
