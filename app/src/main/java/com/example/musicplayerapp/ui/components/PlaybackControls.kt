package com.example.musicplayerapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.example.musicplayerapp.R
import com.example.musicplayerapp.playback.PlaybackState
import com.example.musicplayerapp.ui.player.formatTime

@Composable
fun PlaybackProgress(songId: String, state: PlaybackState, onSeek: (Long) -> Unit) {
    var seekPosition by remember(songId) { mutableStateOf<Float?>(null) }
    val duration = state.durationMs.coerceAtLeast(0)
    val position = seekPosition ?: state.positionMs.toFloat().coerceIn(0f, duration.toFloat())
    val seekLabel = stringResource(R.string.seek)
    Column {
        Slider(
            value = position,
            onValueChange = { seekPosition = it },
            onValueChangeFinished = {
                seekPosition?.let { onSeek(it.toLong()) }
                seekPosition = null
            },
            valueRange = 0f..duration.coerceAtLeast(1).toFloat(),
            enabled = duration > 0 && state.connected,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = seekLabel }
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(position.toLong()), style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatTime(duration), style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PlaybackControls(
    state: PlaybackState,
    isPreview: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit
) {
    val active = MaterialTheme.colorScheme.primary
    val inactive = MaterialTheme.colorScheme.onSurfaceVariant
    val repeatLabel = stringResource(when (state.repeatMode) {
        Player.REPEAT_MODE_ONE -> R.string.repeat_one
        Player.REPEAT_MODE_ALL -> R.string.repeat_all
        else -> R.string.repeat_off
    })
    val shuffleLabel = stringResource(R.string.shuffle)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
        if (isPreview) IconButton(onClick = onShuffle) {
            Icon(Icons.Rounded.Shuffle, stringResource(R.string.random_preview), tint = inactive)
        } else IconToggleButton(
            checked = state.shuffle,
            onCheckedChange = { onShuffle() },
            enabled = isPreview || state.connected
        ) {
            Icon(Icons.Rounded.Shuffle, shuffleLabel, tint = if (state.shuffle) active else inactive)
        }
        IconButton(onClick = onPrevious, enabled = isPreview || (state.connected && state.hasPrevious)) {
            Icon(Icons.Rounded.SkipPrevious, stringResource(R.string.previous), Modifier.size(32.dp))
        }
        FilledIconButton(onClick = onPlayPause, enabled = !isPreview && state.connected,
            modifier = Modifier.size(72.dp)) {
            if (state.isBuffering) CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 2.dp)
            else Icon(if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                stringResource(if (state.isPlaying) R.string.pause else R.string.play), Modifier.size(36.dp))
        }
        IconButton(onClick = onNext, enabled = isPreview || (state.connected && state.hasNext)) {
            Icon(Icons.Rounded.SkipNext, stringResource(R.string.next), Modifier.size(32.dp))
        }
        IconButton(onClick = onRepeat, enabled = !isPreview && state.connected,
            modifier = Modifier.semantics { stateDescription = repeatLabel }) {
            Icon(if (state.repeatMode == Player.REPEAT_MODE_ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                stringResource(R.string.repeat), tint = when {
                    isPreview || !state.connected -> inactive.copy(alpha = 0.38f)
                    state.repeatMode == Player.REPEAT_MODE_OFF -> inactive
                    else -> active
                })
        }
    }
}
