# MusicPlayerApp

An Android music-player UI prototype built with Kotlin and Jetpack Compose.

The app explores a player-style home screen, reactive UI state, and navigation to a details screen using a small, bundled music catalogue. **Audio playback is not implemented.**

Developed by **Enri Sulejmani**.

## Features

- **Player-style interface:** displays cover artwork, a song title, and an artist over a background image.
- **Music selection:** the previous and next buttons each select a random item from four bundled entries. They do not follow a sequential playlist, and the same item can be selected again.
- **Details screen:** tap the cover to view the selected item's title, artist, release information, track count, and description.
- **Back navigation:** return from the details screen to the home screen.
- **Local sample data:** catalogue entries and images are bundled with the app; no account or external API is required.

## Technologies

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · ViewModel · StateFlow

The project uses Android Gradle Plugin **8.6.0**, Kotlin **1.9.0**, and the included Gradle **8.7** wrapper.

## Run locally

### Requirements

- Android Studio with support for Android Gradle Plugin 8.6.0.
- **JDK 17** for Gradle.
- **Android SDK Platform 35**.
- An Android emulator or device running **Android 7.0 (API 24)** or newer.

### Android Studio

1. Clone the repository:

   ```bash
   git clone https://github.com/EnriS2003/MusicPlayerApp.git
   cd MusicPlayerApp
   ```

2. Open the repository root, which contains `settings.gradle.kts`.
3. Set the Gradle JDK to **17**, then sync the project and install any requested SDK components.
4. Select the **app** configuration and a connected device or emulator, then run.

### Command line

With JDK 17 and the Android SDK configured through `ANDROID_HOME` or a local `local.properties` file:

```bash
./gradlew :app:assembleDebug
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Code structure

| File | Responsibility |
| --- | --- |
| [MainActivity.kt](app/src/main/java/com/example/musicplayerapp/MainActivity.kt) | Application entry point and the player-style Compose UI. |
| [MusicAppNavigation.kt](app/src/main/java/com/example/musicplayerapp/navigation/MusicAppNavigation.kt) | Home and details destinations, with values passed through navigation arguments. |
| [DetailsScreen.kt](app/src/main/java/com/example/musicplayerapp/navigation/DetailsScreen.kt) | Displays the selected item's information and a back button. |
| [MusicPlayerViewModel.kt](app/src/main/java/com/example/musicplayerapp/ui/theme/MusicPlayerViewModel.kt) | Holds the sample catalogue and exposes the selected item through StateFlow. |
| [Album.kt](app/src/main/java/com/example/musicplayerapp/model/Album.kt) | Data model for the catalogue entries. |

Compose observes the selected item from the ViewModel and updates the home screen when that state changes.

## Project status and limitations

This is a small learning prototype, preserved for portfolio and reference purposes. Feature development is currently inactive.

- The Play button is a visual placeholder.
- There is no audio engine, streaming integration, playlist management, database, or persistent playback state.
- The catalogue uses illustrative, hardcoded metadata.
- The repository includes Android template tests, rather than a comprehensive application test suite.
- The existing GitHub Actions workflow is a starter example; it does not compile or test the app.

To run the bundled local unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

## Credits and licensing

The sample artist and cover images are included as prototype assets. Their presence in this repository does not grant rights to reuse them.

No open-source license file is currently provided for this project.
