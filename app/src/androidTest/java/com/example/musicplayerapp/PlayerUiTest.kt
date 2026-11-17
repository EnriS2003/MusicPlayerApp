package com.example.musicplayerapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo
import com.example.musicplayerapp.navigation.QueueScreen
import com.example.musicplayerapp.ui.player.PlayerScreen
import com.example.musicplayerapp.ui.player.PlayerUiState
import com.example.musicplayerapp.ui.theme.MusicPlayerAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PlayerUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun previewDoesNotPretendToPlayAudioAndImportIsAvailable() {
        var imported = false
        var advanced = false
        compose.setContent {
            MusicPlayerAppTheme {
                PlayerScreen(PlayerUiState(), {}, {}, { advanced = true }, {}, {}, {}, {}, {}, { imported = true })
            }
        }
        compose.onNodeWithContentDescription("Play").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Next track").performClick()
        compose.runOnIdle { assertEquals(true, advanced) }
        compose.onNodeWithText("Import audio").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, imported) }
    }

    @Test fun queueSearchFiltersByArtistAndSelectionUsesStableId() {
        var selected: String? = null
        compose.setContent {
            MusicPlayerAppTheme { QueueScreen(PlayerUiState(), { selected = it }, {}) }
        }
        compose.onNodeWithText("Search title or artist").performTextInput("Amy")
        compose.onNodeWithText("Tears Dry On Their Own").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals("preview-amy", selected) }
        compose.onNodeWithContentDescription("Clear search").performClick()
        compose.onNodeWithText("Search title or artist").performTextInput("no-such-track")
        compose.onNodeWithText("No matching tracks. Try another title or artist.").assertIsDisplayed()
    }
}
