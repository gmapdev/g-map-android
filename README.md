# GMap - OpenTripPlanner Android Application

**G-MAP — Your journey, your path — Mobility through greater accessibility.**

The Georgia Mobility and Access Planner (G-MAP), developed through a collaboration between the Georgia Department of Transportation, Atlanta Regional Commission, and Gwinnett County, helps travelers find transportation that supports independent living. It focuses on the needs of people with disabilities, older adults, and travelers with limited English proficiency in Gwinnett County.

This repository contains the native Android application, built with Java, Kotlin, Android XML layouts, and OpenTripPlanner (OTP) integration. It provides personalized trip planning, real-time transit information, live trip tracking, and indoor navigation. This guide follows the organization of the companion [GMap iOS project](https://github.com/gmapdev/g-map-ios), with setup and configuration specific to Android.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Quick Start](#quick-start)
- [Architecture](#architecture)
- [Security](#security)
- [Dependencies](#dependencies)
- [Building the Project](#building-the-project)
- [Customization](#customization)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [Support](#support)
- [License](#license)

## Features

- **Trip planning:** Multimodal itineraries with configurable travel modes and routing preferences.
- **Transit information:** Routes, nearby stops, arrival information, vehicle locations, and service alerts.
- **Live trip tracking:** Active route guidance, location updates, and deviation handling.
- **Indoor navigation:** Jibestream maps and navigation with CXApp on-device positioning.
- **Accessibility:** Dedicated accessible layouts, mobility profiles, speech, and vibration feedback.
- **Localization:** String resources for English, Spanish, Korean, Vietnamese, Chinese, Russian, and Tagalog; availability in the app also depends on configuration.
- **User accounts:** Auth0 authentication, favorite locations, saved trips, and notification preferences.
- **Push notifications:** Firebase Cloud Messaging integration for trip notifications.
- **Interactive maps:** Mapbox maps with transit overlays and configurable styling.

Feature availability depends on the deployment configuration and connected services.

## Requirements

The versions below reflect the checked-in Gradle configuration.

| Component | Project setting |
| --- | --- |
| Android Studio | A version compatible with Android Gradle Plugin 8.9.2 |
| Gradle runtime JDK | JDK 17 |
| Gradle | 8.11.1, supplied through the Gradle wrapper |
| Android Gradle Plugin | 8.9.2 |
| Android SDK | Compile and target API 36 |
| Minimum device version | Android 6.0 / API 23 |
| Java / Kotlin bytecode target | 11 |
| Kotlin Gradle plugin | 1.7.10 |
| Product flavor | `otp_its4us` |
| Application ID | `com.ibigroup.mobile.otp.its4us.android` |

Use an Android device or emulator with Google Play services for location and Firebase integrations. Indoor positioning requires a supported physical device and a configured venue. Dependency downloads require network access to the repositories declared in the Gradle files.

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/gmapdev/g-map-android.git
cd g-map-android
```

### 2. Set up Android Studio and the SDK

1. Open the repository root in Android Studio.
2. Install Android SDK Platform 36 and the SDK tools through SDK Manager.
3. Set the Gradle JDK to JDK 17. The Java/Kotlin compilation target of 11 is separate from the JDK that runs Gradle.
4. Let Android Studio create `local.properties` with the local Android SDK path, or configure `ANDROID_HOME` for command-line builds.

### 3. Configure services and sync Gradle

Complete the [Configuration](#configuration) steps, then sync the project. Select `otp_its4usDebug` in Android Studio's **Build Variants** panel.

The repository includes `app/libs/ibilib.jar`, which supplies shared IBI configuration and utility classes. Keep this file available when building. Other dependencies are declared in [app/build.gradle](app/build.gradle).

## Configuration

### Configuration files

| File | Purpose |
| --- | --- |
| [app/src/otp_its4us/assets/json/config.json](app/src/otp_its4us/assets/json/config.json) | Encrypted configuration bundled with the G-MAP flavor |
| [Configurations.java](app/src/otp_its4us/java/com/ibigroup/mobile/otp/android/utility/Configurations.java) | Default endpoints, map bounds, timezone, preferences, and fallback settings |
| [ConfigManager.java](app/src/main/java/com/ibigroup/mobile/otp/android/manager/ConfigManager.java) | Decrypts and parses branding, features, and themes |
| [SecurityManager.kt](app/src/main/java/com/ibigroup/mobile/otp/android/manager/SecurityManager.kt) | Bundled configuration encryption and decryption |
| [Flavor Mapbox resource](app/src/otp_its4us/res/values/mapbox_api_key.xml) | `mapbox_api_key` used to initialize Mapbox |
| [Flavor Auth0 resource](app/src/otp_its4us/res/values/auth0.xml) | `com_auth0_audience` used by authentication requests |
| `app/google-services.json` | Firebase project configuration; supply locally |

### Bundled application configuration

Although it is named `config.json`, the bundled file contains hexadecimal ciphertext, not editable JSON. At startup, `ScreenSplashActivity` loads it when cached branding or theme configuration is missing. `ConfigManager` decrypts the contents and stores the parsed settings through `Configurations`; subsequent launches can reuse cached settings.

The decoded configuration contains these main sections:

| Section | Contents |
| --- | --- |
| `brandInfo` | Environment, API endpoints and keys, Auth0 settings, map center, refresh intervals, and Android vibration settings |
| `feature` | Named features with a `detail` object, including `is_enabled` and feature-specific options |
| `theme` | Light/dark skin selection and templates for colors and fonts |
| `revision` | Configuration revision |

Start from the deployment's complete configuration. For example, the following is a **partial plaintext excerpt**, not a replacement for the encrypted asset:

```json
{
  "brandInfo": {
    "app_identifier": "gmap",
    "environment": "staging",
    "base_url": {
      "staging": "https://staging.example.com/",
      "production": "https://api.example.com/"
    },
    "auth0_domain": "https://your-tenant.auth0.com",
    "auth0_client_id": "YOUR_AUTH0_CLIENT_ID",
    "latitude_of_map_center": 33.966016,
    "longitude_of_map_center": -84.031541,
    "android_vibration_duration_ms": 250,
    "android_vibration_intensity": 70
  },
  "feature": {
    "Search": {
      "detail": {
        "is_enabled": true,
        "enable_start_route": true,
        "selectable_modes": "TRANSIT:BUS,SUBWAY,STREETCAR;BICYCLE;CAR"
      }
    }
  }
}
```

To replace the bundled configuration:

1. Update the complete plaintext configuration for your deployment, preserving required feature and theme sections.
2. Encrypt it using the format implemented by `SecurityManager.encrypt`: Base64-encode the plaintext, encrypt that text using AES/CBC/PKCS7 padding, then encode the ciphertext as hexadecimal. The encryption key and IV must match `SecurityManager.kt`.
3. Save the encrypted text to `app/src/otp_its4us/assets/json/config.json` and rebuild.
4. When validating a changed asset, clear the development app's storage or reinstall it so cached configuration does not mask the change. This removes local app data.

This Android repository does not include the iOS `generate_encrypted_config.py` utility. Check any external configuration generator against the Android encoding and encryption format before using its output.

Review endpoint defaults in `Configurations.java` as well as the JSON configuration. For example, `GRAPHQL_SERVER_URL` appends `otp/routers/default/index/graphql` to `app_domain`, and autocomplete has its own `AUTOCOMPLETE_DOMAIN` constant. The Android configuration parser determines which shared configuration fields are actually applied.

### Mapbox

Set `mapbox_api_key` in the flavor's `res/values/mapbox_api_key.xml`. The flavor resource overrides the resource with the same name in `app/src/main/res/values/`.

The Gradle files also declare Mapbox's Maven repository with Basic authentication, but do not supply repository credentials. If dependency resolution requires authentication, configure it using your deployment's Mapbox download credentials and local Gradle properties. A runtime map token and Maven download credentials serve different purposes.

### Auth0

Set `auth0_domain` and `auth0_client_id` in the encrypted configuration and update `com_auth0_audience` in the flavor's `auth0.xml`. Review the `auth0Domain` and `auth0Scheme` manifest placeholders in `app/build.gradle` when configuring redirect-based authentication; the checked-in domain placeholder is empty.

### Firebase

Provide a Firebase Android configuration at `app/google-services.json` for the application ID `com.ibigroup.mobile.otp.its4us.android`. This file is not included in the repository. The app applies the Google Services and Crashlytics Gradle plugins and initializes Firebase at startup.

### Indoor navigation

Configure the `Indoor Navigation` feature with your venue's Jibestream endpoint and credentials, CXApp positioning settings, and entrance/exit data. Review the indoor data URL defaults in `Configurations.java` when changing deployments.

## Quick Start

After completing installation and configuration:

1. Select the `app` run configuration and `otp_its4usDebug` build variant.
2. Choose a device or emulator running API 23 or later.
3. Run the app from Android Studio, or install it with:

   ```bash
   ./gradlew :app:installOtp_its4usDebug
   ```

4. Open **G-MAP** and grant the permissions needed for the features you are testing, including location and notifications when requested.
5. Verify that the map loads, plan a trip, and test sign-in and saved trips with your configured services.

## Architecture

The app uses Android activities and fragments with XML layouts and View Binding. Shared application state and request queues live in `MyApplication`; managers and utility classes handle configuration, location, and service requests. Apollo generates GraphQL client types from the checked-in queries and schema.

```text
g-map-android/
├── app/
│   ├── build.gradle                 # Android build, flavor, and dependencies
│   ├── libs/ibilib.jar              # Shared IBI library
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml  # Components and permissions
│       │   ├── java/com/ibigroup/mobile/otp/android/
│       │   │   ├── MyApplication.java
│       │   │   ├── api/             # API resource definitions
│       │   │   ├── config/          # Theme and feature models
│       │   │   ├── fragment/        # Main UI fragments
│       │   │   ├── manager/         # Configuration and location services
│       │   │   ├── json/            # Request and response models
│       │   │   ├── fcm/             # Firebase messaging service
│       │   │   └── utility/         # Networking and shared helpers
│       │   ├── graphql/            # Apollo queries and schema
│       │   ├── assets/             # Shared bundled data
│       │   └── res/                # Layouts, strings, styles, and images
│       ├── otp_its4us/              # G-MAP configuration and resource overrides
│       ├── test/                    # Local unit tests
│       └── androidTest/             # Device/emulator tests
├── build.gradle                     # Build plugins and repositories
├── settings.gradle                  # Single app module; root project name: otp
├── gradle.properties
└── gradle/wrapper/                   # Pinned Gradle distribution
```

## Security

The bundled configuration uses the AES implementation in `SecurityManager.kt`. Preference helpers in `Configurations.java` also use `IBISecurity` from `ibilib.jar`. These are separate code paths; changing the bundled configuration key does not change the library's preference encryption.

For a new deployment, replace the bundled configuration key and IV together with the encrypted asset. Keys embedded in an Android application can be extracted, so configuration encryption does not make embedded credentials server-side secrets. Keep privileged credentials on the backend and avoid committing plaintext configuration, signing keys, or local service credentials.

Release builds currently use `signingConfigs.debug`. Configure a release keystore and signing configuration before distributing the application.

## Dependencies

Selected versions declared in `app/build.gradle`:

| Library | Version | Purpose |
| --- | --- | --- |
| Mapbox Android SDK | 9.6.1 | Interactive outdoor maps |
| Apollo Android | 2.5.14 | GraphQL client and code generation |
| Auth0 Lock | 3.2.1 | Authentication integration |
| Jibestream Android SDK | 6.0.4 | Indoor maps |
| Jibestream Map UI Kit / Navigation Kit | 1.1.3 / 2.0.1 | Indoor UI and guidance |
| CXApp Extend | 1.5.6.6 | Indoor positioning |
| Firebase Messaging | 23.1.2 | Push notifications |
| Firebase Crashlytics | 18.0.0 | Crash reporting |
| Google Play services Location | 18.0.0 | Device location |
| AndroidX WorkManager | 2.7.1 | Background work |
| Volley / Gson | 1.0.19 / 2.8.6 | HTTP requests and JSON parsing |
| JUnit / Espresso | 4.13.2 / 3.5.1 | Unit and UI testing |

Gradle manages remote dependencies; `ibilib.jar` is included locally. Repository declarations include Google Maven, Maven Central, JCenter, Mapbox, and CXApp/Jibestream Artifactory. Changes to repository access or artifact availability may require build configuration updates.

## Building the Project

Run commands from the repository root. On Windows, use `gradlew.bat` instead of `./gradlew`.

### Development build

```bash
./gradlew :app:assembleOtp_its4usDebug
```

Debug APKs are written under `app/build/outputs/apk/otp_its4us/debug/`.

### Release build

After configuring release signing:

```bash
# APK
./gradlew :app:assembleOtp_its4usRelease

# Android App Bundle
./gradlew :app:bundleOtp_its4usRelease
```

Release APKs are written under `app/build/outputs/apk/otp_its4us/release/`; bundles are under `app/build/outputs/bundle/`. The build customizes release APK filenames. Flavor values override default version settings, so update the `otp_its4us` version code and name in `app/build.gradle` for releases.

### Checks

```bash
./gradlew :app:testOtp_its4usDebugUnitTest
./gradlew :app:lintOtp_its4usDebug

# Requires a connected device or running emulator
./gradlew :app:connectedOtp_its4usDebugAndroidTest
```

The repository includes starter unit and instrumentation tests. These do not establish coverage of trip planning or navigation. Lint currently has `abortOnError false` and release checks disabled, so review the report even when a build succeeds.

## Customization

- **Branding:** Update flavor resources in `app/src/otp_its4us/res/`, including strings, colors, and launcher assets, together with the encrypted `brandInfo` and `theme` sections.
- **Features:** Adjust feature `detail` settings and `is_enabled` flags, then confirm they are handled in `ConfigManager` and the relevant screens.
- **Service area:** Review the map center, bounds, timezone, autocomplete endpoint, and transit service URLs in the configuration and `Configurations.java`.
- **Transit modes:** Update the flavor's JSON files in `assets/json/`, such as `mode_config.json`, `mode_combinations.json`, and `route_mode_mapping.json`.
- **Localization and accessibility:** Update shared `res/values*/strings.xml` resources and the corresponding standard and accessible layouts.
- **GraphQL:** Update queries and schema under `app/src/main/graphql/` when the backend contract changes; regenerate client code through the Gradle build.

## Contributing

1. Create a branch for your change.
2. Follow the existing Java/Kotlin conventions and resource organization.
3. Add or update tests appropriate to the behavior being changed.
4. Run the relevant build, tests, and lint checks.
5. For UI or navigation changes, test on a device and review accessibility with TalkBack, larger text, and relevant permission states.
6. Submit a pull request describing the change, validation, and any service configuration requirements.

## Troubleshooting

| Symptom | What to check |
| --- | --- |
| SDK location or platform missing | Configure the local SDK path and install Platform 36 through SDK Manager. |
| Gradle fails before compilation | Confirm the Gradle runtime uses JDK 17 and use the checked-in wrapper. |
| Missing `google-services.json` or no matching Firebase client | Supply the Firebase configuration matching the flavor application ID. |
| Dependency download or authentication failure | Check the failing repository, network access, artifact availability, and any required repository credentials. |
| Missing IBI classes | Confirm `app/libs/ibilib.jar` is present. |
| Map does not load | Check the flavor's `mapbox_api_key`, network connectivity, and Logcat errors. |
| Configuration fails to load | Check the hexadecimal ciphertext, Base64/AES format, matching key and IV, and decoded JSON structure. |
| Updated asset settings do not appear | Cached preferences may still be active; clear development app storage or reinstall to test a fresh launch. |
| Sign-in fails | Check Auth0 domain, client ID, audience, and the configured authentication flow. |
| Location or indoor positioning fails | Check device location settings, runtime permissions, Google Play services, and venue/positioning configuration. |
| Notifications do not arrive | Check Firebase configuration, notification permission, device registration, and the push backend. |

## Support

Report bugs and feature requests through the [Android repository's issue tracker](https://github.com/gmapdev/g-map-android/issues). Include the build variant, device/Android version, reproduction steps, and relevant logs with credentials and personal information removed.

## License

This repository does not currently include a `LICENSE` file. Confirm licensing and redistribution terms with the project maintainers before reuse. Third-party libraries retain their respective licenses.

## Acknowledgments

- Georgia Department of Transportation, Atlanta Regional Commission, and Gwinnett County
- OpenTripPlanner community
- Mapbox, Auth0, Firebase, and Jibestream/CXApp
- Project contributors and maintainers
