package com.example.musicplayerapp

import MusicAppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.ui.theme.MusicPlayerAppTheme
import com.example.musicplayerapp.ui.theme.MusicPlayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerAppTheme {
                MusicAppNavigation()
            }
        }
    }
}


@Composable
fun AppImg(
    navController: NavHostController,
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val album by viewModel.uiState.collectAsState()

    // Resources for background and button images
    val background = painterResource(R.drawable.backg3)
    val lxImg = painterResource(R.drawable.next2)
    val dxImg = painterResource(R.drawable.next1)
    val centerImg = painterResource(R.drawable.play_button)

    // Define layout with a Box containing the album image and buttons
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = background,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Background overlay with gradient color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x99000000),
                            Color(0x99000000),
                        ),
                    )
                )
        ) {
            // Column with album image + song name and artist name
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = modifier
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Album Image with onclick listener
                        Image(
                            painter = painterResource(album.id),
                            contentDescription = "Album Cover",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    navController.navigate(
                                        "details/${album.songName}/${album.artistName}/${album.releaseMonth}/${album.releaseYear}/${album.trackCount}/${album.description}"
                                    )
                                }
                        )
                        // Song name text
                        Text(
                            text = album.songName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = colorResource(R.color.white),
                            modifier = Modifier
                                .padding(top = 30.dp, bottom = 10.dp)
                                .align(Alignment.CenterHorizontally)
                                .width(350.dp),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // Artist name text
                        Text(
                            text = album.artistName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = colorResource(R.color.white),
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 140.dp)
                                .align(Alignment.CenterHorizontally)
                                .width(350.dp),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Row for the player buttons
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Button for the previous song
                    Button(
                        onClick = { viewModel.randomizeAlbum() },
                        modifier = Modifier.size(70.dp), // Uniform button size
                        shape = androidx.compose.foundation.shape.CircleShape, // Circular shape
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.teal_200),
                            contentColor = colorResource(R.color.white)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            10.dp
                        )
                    ) {
                        Image(
                            painter = lxImg,
                            contentDescription = "Previous",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    // Button for the play action
                    Button(
                        onClick = { /* Action for center button */ },
                        modifier = Modifier.size(70.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.teal_200),
                            contentColor = colorResource(R.color.white)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            10.dp
                        )
                    ) {
                        Image(
                            painter = centerImg,
                            contentDescription = "Play",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    // Button for the next song
                    Button(
                        onClick = { viewModel.randomizeAlbum() },
                        modifier = Modifier.size(70.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.teal_200),
                            contentColor = colorResource(R.color.white)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            10.dp
                        )
                    ) {
                        Image(
                            painter = dxImg,
                            contentDescription = "Next",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } // Close row
            } // Close column
        } // Close background overlay
    } // Close main Box
}

// Function just for a preview
@Preview(showBackground = true)
@Composable
fun MusicAppPreview() {
    MusicPlayerAppTheme {
        val fakeNavController = rememberNavController()
        val previewViewModel = MusicPlayerViewModel()
        previewViewModel.randomizeAlbum()

        // Pass the fake NavController and ViewModel to AppImg
        AppImg(navController = fakeNavController, viewModel = previewViewModel)
    }
}
