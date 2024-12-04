import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.AppImg
import com.example.musicplayerapp.ui.theme.MusicPlayerViewModel
import com.example.musicplayerapp.ui.DetailsScreen

@Composable
fun MusicAppNavigation() {
    val navController = rememberNavController()
    val musicPlayerViewModel: MusicPlayerViewModel = viewModel() // Obtain ViewModel

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            AppImg(navController = navController, viewModel = musicPlayerViewModel)
        }
        composable("details/{songName}/{artistName}/{releaseMonth}/{releaseYear}/{trackCount}/{description}") { backStackEntry ->
            val songName = backStackEntry.arguments?.getString("songName")
            val artistName = backStackEntry.arguments?.getString("artistName")
            val releaseMonth = backStackEntry.arguments?.getString("releaseMonth")
            val releaseYear = backStackEntry.arguments?.getString("releaseYear")?.toIntOrNull()
            val trackCount = backStackEntry.arguments?.getString("trackCount")?.toIntOrNull()
            val description = backStackEntry.arguments?.getString("description")

            DetailsScreen(
                navController = navController,
                songName = songName,
                artistName = artistName,
                releaseMonth = releaseMonth,
                releaseYear = releaseYear,
                trackCount = trackCount,
                description = description
            )

        }
    }
}