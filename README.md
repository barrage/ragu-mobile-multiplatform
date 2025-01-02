## About the Project

**Ragu Multiplatform**  is a Kotlin Multiplatform chat application demonstrating real-time messaging
capabilities on Android and iOS platforms. It highlights the power of shared code
using Compose Multiplatform and Kotlin Multiplatform.

### Screenshots

<div align="center"> 
  <img src="https://placehold.co/600x400?text=App+Screenshot+Here" alt="Ragu Multiplatform Screenshot" />
</div>

## Features

- Real-time messaging across Android and iOS platforms
- Shared codebase using Kotlin Multiplatform
- Modern UI with Compose Multiplatform

---

### Tech Stack

- [**Compose Multiplatform**](https://github.com/JetBrains/compose-multiplatform)
    - Used for building the UI across all platforms.

- [**Koin**](https://github.com/InsertKoinIO/koin):
    - Handles dependency injection for services and view models across platforms.

- [**Ktor**](https://ktor.io/):
    - Manages network requests and WebSocket connections for real-time messaging.

- [**Kotlinx Serialization**](https://github.com/Kotlin/kotlinx.serialization):
    - Handles JSON serialization and deserialization.

- [**Kotlinx DateTime**](https://github.com/Kotlin/kotlinx-datetime):
    - Manages date and time operations across platforms.

- [**Napier**](https://github.com/AAkira/Napier):
    - Provides logging capabilities across platforms.

- [**Landscapist Coil**](https://github.com/skydoves/landscapist):
    - Handles image loading and caching.

- [**Compottie**](https://github.com/alexzhirkevich/compottie):
    - Used for loading and displaying vector animations.

- [**Konnection**](https://github.com/TM-Apps/konnection):
    - Manages internet connection status and network changes.

- [**Firebase Kotlin SDK**](https://github.com/GitLiveApp/firebase-kotlin-sdk):
    - Integrates Firebase services, including Crashlytics.

- [**MaterialKolor**](https://github.com/jordond/MaterialKolor):
    - Generates dynamic color palettes for Material 3 theming, enhancing the app's visual
      customization capabilities.

- [**Multiplatform DataStore**](https://developer.android.com/kotlin/multiplatform/datastore):
    - A Kotlin Multiplatform library for data storage that works across Android, iOS, and desktop
      platforms.
- [**Markdown Renderer**](https://github.com/mikepenz/multiplatform-markdown-renderer):
    - Markdown text rendering and syntax highlighting.

---

## Project Structure and Architecture

### Project Structure

The project follows a typical Kotlin Multiplatform structure:

- `composeApp/`: Contains the shared code and platform-specific implementations
    - `src/commonMain/`: Shared Kotlin code for all platforms
    - `src/androidMain/`: Android-specific code
    - `src/iosMain/`: iOS-specific code
- `iosApp/`: iOS application module
- `gradle/`: Gradle configuration files
- `build.gradle.kts`: Main Gradle build script

#### Configuration Constants

The application uses a set of configuration constants defined in
`composeApp/src/commonMain/kotlin/net/barrage/ragu/utils/Constants.kt`. These constants include:

##### API Configuration

- `BASE_URL`: The base URL for the API
    - Current value: `"llmao-kotlin-api-development.barrage.dev"`

##### Authentication Constants

- `REDIRECT_PATH`: The path for OAuth redirection
    - Current value: `"/oauthredirect"`
- `REDIRECT_URI`: The full redirect URI for the OAuth flow
    - Current value: `"https://llmao-kotlin-api-development.barrage.dev/oauthredirect"`

The `Constants.kt` file also includes private constants for Google OAuth configuration, such as the
authorization URL, client ID, response type, and requested scopes.

**Note:** Ensure that the BASE_URL and REDIRECT_URI are correctly set for your environment. Update
these values in the `Constants.kt` file when deploying to your environment.

### Architecture

This project follows a Clean Architecture approach with MVVM (Model-View-ViewModel) for the
presentation layer:

1. **Presentation Layer** (`ui/` directory):
    - Uses Compose Multiplatform for the UI
    - ViewModels handle UI logic and state management

2. **Domain Layer** (`domain/` directory):
    - Contains business logic and use cases
    - Defines repository interfaces

3. **Data Layer** (`data/` directory):
    - Implements repository interfaces
    - Manages data sources (local storage, network)

### Key Components

- **Dependency Injection**: Koin is used for dependency injection across the app.
- **Navigation**: Implemented using Compose Navigation.
- **Networking**: Ktor handles API requests and WebSocket connections.
- **State Management**: Kotlin Flows and StateFlow for reactive state management.
- **Concurrency**: Coroutines and Flows for asynchronous operations.

---

## Getting Started

### Prerequisites

**Check Your Development Environment**:
Follow the instructions provided in the official Kotlin documentation to set up your environment
for Kotlin Multiplatform
development: [Multiplatform Setup](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-setup.html#check-your-environment).

---

### Run the app

#### Android

- Open the project in Android Studio.

- Run the `composeApp` configuration.

#### iOS

- Open the project in Xcode.

- Build and run.

---

### Android App Signing

This project uses a `keystore.properties` file for secure Android app signing. This file is not
included in the repository for security reasons.

#### Setting up keystore.properties

1. Create a file named `keystore.properties` in the root directory of the project.
2. Add the following properties to the file:
    ```
    storeFile=path/to/your/keystore.jks
    storePassword=your_store_password
    keyAlias=your_key_alias
    keyPassword=your_key_password
    ```
3. Replace the values with your actual keystore information:
    - `storeFile`: Path to your keystore file (e.g., `release_keystore.jks`)
    - `storePassword`: Password for your keystore
    - `keyAlias`: Alias of the key in the keystore
    - `keyPassword`: Password for the key

#### Security Note

- Never commit the `keystore.properties` file or your actual keystore to version control.
- Keep your keystore and its passwords secure and private.

The app's `build.gradle.kts` file is configured to use these properties for signing the release
version of the app. If the `keystore.properties` file is not found, an empty signing configuration
will be created, allowing the build process to continue without signing.

---

## Usage

This project serves as a starting point for building custom chat solutions. Modify the UI, integrate
with your backend, or more.

---

## License

This repository contains Multiplatform app, a part of Ragu, covered under
the [Apache License 2.0](LICENSE), except where noted (any Ragu logos or trademarks are not covered
under the Apache License, and should be explicitly noted by a LICENSE file.)

Multiplatform app, a part of Ragu, is a product produced from this open source software, exclusively
by Barrage d.o.o. It is distributed under our commercial terms.

Others are allowed to make their own distribution of the software, but they cannot use any of the
Ragu trademarks, cloud services, etc.

We explicitly grant permission for you to make a build that includes our trademarks while developing
Ragu itself. You may not publish or share the build, and you may not use that build to run Ragu for
any other purpose.