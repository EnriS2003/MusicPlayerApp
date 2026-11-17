package com.example.musicplayerapp.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.ui.player.MusicPlayerViewModel
import com.example.musicplayerapp.ui.player.PlayerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicAppNavigation(viewModel: MusicPlayerViewModel = viewModel()) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "player"
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
        viewModel.importAudio(it)
    }
    val importAudio = { importer.launch(arrayOf("audio/*")) }
    fun navigateTopLevel(destination: String) {
        navController.navigate(destination) {
            popUpTo("player") { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    fun showDetails(id: String) { navController.navigate("details/${Uri.encode(id)}") }
    fun selectSong(id: String) { viewModel.selectSong(id); navigateTopLevel("player") }

    LaunchedEffect(state.importResult) {
        val result = state.importResult ?: return@LaunchedEffect
        val message = when {
            result.failed > 0 -> context.getString(R.string.import_partial, result.added, result.failed)
            result.added == 0 -> context.getString(R.string.import_duplicates)
            else -> context.resources.getQuantityString(R.plurals.import_success, result.added, result.added)
        }
        snackbar.showSnackbar(message)
        viewModel.clearImportResult()
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(title = { Text(stringResource(when (route) {
                "queue" -> R.string.queue_title
                "details/{songId}" -> R.string.song_details
                else -> R.string.app_name
            })) }, navigationIcon = {
                if (route.startsWith("details")) IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.back))
                }
            }, actions = {
                if (state.importing) CircularProgressIndicator()
                else IconButton(onClick = importAudio) {
                    Icon(Icons.Rounded.Add, stringResource(R.string.import_audio))
                }
            })
        },
        bottomBar = {
            if (!route.startsWith("details")) NavigationBar {
                NavigationBarItem(selected = route == "player", onClick = { navigateTopLevel("player") },
                    icon = { Icon(Icons.Rounded.Album, null) }, label = { Text(stringResource(R.string.player_title)) })
                NavigationBarItem(selected = route == "queue", onClick = { navigateTopLevel("queue") },
                    icon = { Icon(Icons.AutoMirrored.Rounded.QueueMusic, null) }, label = { Text(stringResource(R.string.queue_title)) })
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        NavHost(navController, startDestination = "player",
            modifier = Modifier.padding(padding).consumeWindowInsets(padding).imePadding()) {
            composable("player") {
                PlayerScreen(state, viewModel::togglePlay, viewModel::previous, viewModel::next,
                    viewModel::shuffle, viewModel::cycleRepeat, viewModel::seekTo,
                    ::showDetails, { navigateTopLevel("queue") }, importAudio)
            }
            composable("queue") { QueueScreen(state, ::selectSong, ::showDetails) }
            composable("details/{songId}", arguments = listOf(navArgument("songId") { type = NavType.StringType })) { backStack ->
                val id = backStack.arguments?.getString("songId")
                DetailsScreen(state.songs.find { it.id == id }, ::selectSong) { songId ->
                    viewModel.removeSong(songId)
                    navController.navigateUp()
                }
            }
        }
    }
}
