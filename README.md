# DARKI GLASS / Stand-Dock

A landscape-first Android notification dashboard designed to be mirrored to a Windows laptop using Vivo Office Kit.

## Features
- Dark, crimson-accented Liquid Glass-inspired landscape dashboard.
- Live clock and date.
- Notification listener integration with explicit Android notification-access permission.
- Layered notification cards and an expandable notification history.
- Local-only preferences and notification history.
- GitHub Actions workflow to build and upload a debug APK.

## Build
Open this repository in Android Studio with JDK 17 and Android SDK 34 installed. Sync Gradle and run the app, or use **Build > Build APK(s)**.

GitHub Actions also builds a debug APK on pushes to `main` and can be started manually from the Actions tab. Download the APK from the workflow run's artifacts.

## Permissions and privacy
The app does not request Internet access. Notification access is optional and must be enabled manually in Android settings. Notification text and history stay on the device. Dismissing a dashboard card does not dismiss the original Android notification.

## Mirroring
DARKI GLASS does not implement screen capture or mirroring. Open the app in landscape and use Vivo Office Kit to mirror the phone screen. Appearance may depend on the mirroring mode and aspect ratio.
