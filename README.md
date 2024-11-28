
---
<!-- Table of Contents -->

# Table of Contents

- [About the Project](#about-the-project)
    - [Screenshots](#screenshots)

    - [Tech Stack](#tech-stack)

    - [Features](#features)

- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)

- [Usage](#usage)

---

## About the Project

**ChatWhitelabel**  is a Kotlin Multiplatform chat application demonstrating real-time messaging
capabilities across Android, iOS, and Desktop platforms. It highlights the power of shared code
using Compose Multiplatform and Kotlin Multiplatform.

### Screenshots

<div align="center"> 
  <img src="https://placehold.co/600x400?text=App+Screenshot+Here" alt="ChatWhitelabel Screenshot" />
</div>

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

- [**Kotlinx Coroutines**](https://github.com/Kotlin/kotlinx.coroutines):
    - Handles asynchronous programming.

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

---

### Features

- Real-time messaging using **Ktor WebSockets**

- Cross-platform UI with **Compose Multiplatform**

- Clean Architecture with MVVM

- Dependency Injection using **Koin**

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

#### Desktop

- Use the `desktopMain` Gradle configuration or run via your IDE.

---

## Usage

This project serves as a starting point for building custom chat solutions. Modify the UI, integrate
with your backend, or more.

---