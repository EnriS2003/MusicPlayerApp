package com.example.musicplayerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun DetailsScreen(
    navController: NavHostController,
    songName: String?,
    artistName: String?,
    releaseMonth: String?,
    releaseYear: Int?,
    trackCount: Int?,
    description: String?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), //space for button
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Album Details",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Song: $songName",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Artist: $artistName",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Release Date: $releaseMonth $releaseYear",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Number of Tracks: $trackCount",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Description: $description",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp),
                    textAlign = TextAlign.Justify
                )
        }
        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(50.dp)
        ) {
            Text("Back to Home")
        }
    }
}