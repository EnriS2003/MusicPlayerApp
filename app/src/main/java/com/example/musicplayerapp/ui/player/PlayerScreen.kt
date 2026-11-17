package com.example.musicplayerapp.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.R
import com.example.musicplayerapp.ui.components.AlbumArtwork
import com.example.musicplayerapp.ui.components.PlaybackControls
import com.example.musicplayerapp.ui.components.PlaybackProgress
import com.example.musicplayerapp.ui.theme.rememberArtworkGradient

@Composable
fun PlayerScreen(
    state: PlayerUiState,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onSeek: (Long) -> Unit,
    onDetails: (String) -> Unit,
    onQueue: () -> Unit,
    onImport: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(rememberArtworkGradient(state.currentSong))) {
        val landscape = maxWidth > 600.dp && maxHeight < 600.dp
        val coverSize = (maxHeight * 0.38f).coerceIn(160.dp, 320.dp)
        val landscapeCoverSize = (maxHeight - 32.dp).coerceIn(96.dp, 320.dp)
        val artwork: @Composable (Modifier) -> Unit = { modifier ->
            AlbumArtwork(state.currentSong, modifier.aspectRatio(1f).clickable(
                onClickLabel = stringResource(R.string.song_details)
            ) { onDetails(state.currentSong.id) })
        }
        val controls: @Composable () -> Unit = {
            val transport: @Composable () -> Unit = {
                PlaybackControls(state.playback, state.isPreview, onPlayPause, onPrevious, onNext, onShuffle, onRepeat)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (landscape) 8.dp else 16.dp)) {
                Text(state.currentSong.title, style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center)
                Text(state.currentSong.artist, style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                if (landscape) transport()
                if (state.isPreview) {
                    Text(stringResource(R.string.preview_hint), style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                } else {
                    PlaybackProgress(state.currentSong.id, state.playback, onSeek)
                }
                if (!landscape) transport()
                if (state.playback.error && !state.isPreview) {
                    Text(stringResource(R.string.playback_error), color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
                if (state.isPreview) {
                    Button(onClick = onImport, enabled = !state.importing) {
                        Icon(Icons.Rounded.Add, null)
                        Text(stringResource(R.string.import_audio), Modifier.padding(start = 8.dp))
                    }
                }
                if (!landscape) OutlinedButton(onClick = onQueue) {
                    Icon(Icons.AutoMirrored.Rounded.QueueMusic, null)
                    Text(stringResource(R.string.open_queue), Modifier.padding(start = 8.dp))
                }
            }
        }
        if (landscape) {
            Row(Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    artwork(Modifier.widthIn(max = landscapeCoverSize).fillMaxWidth())
                }
                Box(Modifier.weight(1f).verticalScroll(rememberScrollState())) { controls() }
            }
        } else {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(stringResource(if (state.isPreview) R.string.artwork_preview else R.string.now_playing),
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(24.dp))
                artwork(Modifier.widthIn(max = coverSize).fillMaxWidth())
                Spacer(Modifier.height(28.dp))
                Box(Modifier.widthIn(max = 440.dp).fillMaxWidth()) { controls() }
            }
        }
    }
}
