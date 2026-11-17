package com.example.musicplayerapp.ui.theme

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import com.example.musicplayerapp.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Decode and sample artwork away from the main thread; darken it to keep text legible. */
@Composable
fun rememberArtworkGradient(song: Song): Brush {
    val context = LocalContext.current
    val fallback = Color(0xFF293D3B)
    val tint by produceState(fallback, song.artworkUri, song.artworkRes) {
        value = fallback
        val model = song.artworkUri ?: song.artworkRes
        if (model != null) {
            value = withContext(Dispatchers.IO) {
                runCatching {
                    val drawable = context.imageLoader.execute(
                        ImageRequest.Builder(context).data(model).size(96)
                            .allowHardware(false).bitmapConfig(Bitmap.Config.ARGB_8888).build()
                    ).drawable as? BitmapDrawable
                    drawable?.bitmap?.let {
                        val color = Palette.from(it).maximumColorCount(8).generate()
                            .getDominantColor(fallback.toArgb())
                        androidx.compose.ui.graphics.lerp(Color.Black, Color(color), 0.4f)
                    } ?: fallback
                }.getOrDefault(fallback)
            }
        }
    }
    return Brush.verticalGradient(listOf(tint, Color(0xFF101319)))
}
