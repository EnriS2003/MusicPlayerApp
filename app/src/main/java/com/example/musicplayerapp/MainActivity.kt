package com.example.musicplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.example.musicplayerapp.ui.theme.MusicPlayerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerAppTheme{
                // A surface container making the background filling the size of the layout
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppImg(
                        title = "Elvis Presley",
                    )
                }
            }
        }
    }
}

@Composable
fun AppImg(title: String, modifier: Modifier = Modifier) {

    // Defining the images from resources
    val background = painterResource(R.drawable.backg3)
    val lxImg = painterResource(R.drawable.next2)
    val dxImg = painterResource(R.drawable.next1)
    val centerImg = painterResource(R.drawable.play_button)

    var result by remember { mutableStateOf(1) }
    // Map each result to an album image
    val albumResource = when (result) {
        1 -> R.drawable.adele
        2 -> R.drawable.amy
        3 -> R.drawable.bruce
        4 -> R.drawable.elvis1
        else -> R.drawable.citt_incantata
    }

    // Map each result to a description
    val albumText = when (result) {
        1 -> "Adele - Cry Your Heart Out"
        2 -> "Amy Winehouse - Tears Dry On Their Own"
        3 -> "Bruce Springsteen - Born In The U.S.A."
        4 -> "Elvis Presley - Blue Suede Shoes"
        else -> "Joe Hisaishi - The Last Station"
    }

    // Defining layout with boxes, rows and columns
    Box(
        modifier = modifier.fillMaxSize()
    ){
        // Background image
        Image(
            painter = background,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // backround color
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
        )
        {
            // Column having the album image and text
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = modifier
                )
                {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Album Image
                        Image(
                            painter = painterResource(albumResource),
                            contentDescription = "Center Image",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        // Album text
                        Text(
                            text = albumText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = colorResource(R.color.white),
                            modifier = Modifier
                                .padding(top = 30.dp, bottom = 140.dp)
                                .align(Alignment.CenterHorizontally)
                                .width(350.dp),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Row for the buttons of the player app
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            var newResult: Int
                            do {
                                newResult = (1..4).random()
                            } while (newResult == result)
                            result = newResult
                        },
                        modifier = Modifier.size(70.dp), // Uniform button size
                        shape = androidx.compose.foundation.shape.CircleShape, // Circular shape
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.teal_200),
                            contentColor = colorResource(R.color.white)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(10.dp)
                    ) {
                        Image(
                            painter = lxImg,
                            contentDescription = "Previous",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Button(
                        onClick = { /* Center button action */ },
                        modifier = Modifier.size(70.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.teal_200),
                            contentColor = colorResource(R.color.white)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(10.dp)
                    ) {
                        Image(
                            painter = centerImg,
                            contentDescription = "Previous",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Button(
                        onClick = {
                            var newResult: Int
                            do {
                                newResult = (1..4).random()
                            } while (newResult == result)
                            result = newResult
                        },
                        modifier = Modifier.size(70.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.teal_200),
                            contentColor = colorResource(R.color.white)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(10.dp)
                    ) {
                        Image(
                            painter = dxImg,
                            contentDescription = "Previous",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                } // Close row
            } // Close column
        } // Close box with album image text and buttons
    } // Close box with background image
}

@Preview(showBackground = true)
@Composable
fun MusicAppPreview() {
    MusicPlayerAppTheme {
        AppImg(
            title = "Elvis Presley"
        )
    }
}