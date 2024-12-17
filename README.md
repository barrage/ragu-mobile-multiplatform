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

## Usage

This project serves as a starting point for building custom chat solutions. Modify the UI, integrate
with your backend, or more.

---

## License

This repository contains Multiplatform app, a part of Ragu, covered under the [Apache License 2.0](LICENSE), except where noted (any Ragu logos or trademarks are not covered under the Apache License, and should be explicitly noted by a LICENSE file.)

Multiplatform app, a part of Ragu, is a product produced from this open source software, exclusively by Barrage d.o.o. It is distributed under our commercial terms.

Others are allowed to make their own distribution of the software, but they cannot use any of the Ragu trademarks, cloud services, etc.

We explicitly grant permission for you to make a build that includes our trademarks while developing Ragu itself. You may not publish or share the build, and you may not use that build to run Ragu for any other purpose.