import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.AppImg
import com.example.musicplayerapp.ui.theme.MusicPlayerViewModel
import com.example.musicplayerapp.navigation.DetailsScreen

@Composable
fun MusicAppNavigation() {
    val navController = rememberNavController()
    val musicPlayerViewModel: MusicPlayerViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            AppImg(navController = navController, viewModel = musicPlayerViewModel)
        }
        composable("details/{albumId}/{albumText}") { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId")
            val albumText = backStackEntry.arguments?.getString("albumText")
            DetailsScreen(navController, albumId, albumText)
        }
    }
}