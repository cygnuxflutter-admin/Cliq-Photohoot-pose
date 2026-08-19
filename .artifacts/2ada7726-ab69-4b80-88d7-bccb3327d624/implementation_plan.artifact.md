# Implementation Plan - Fix "other has different root" and BuildConfig Deprecation

The "'other' has different root" error persists during signed bundle generation because Android Studio passes temporary paths across different drive roots. Upgrading to AGP 8.7.3+ (which requires Gradle 8.9+) is the recommended fix.

Additionally, I will resolve the deprecation warning for `buildConfig`.

## User Review Required

> [!IMPORTANT]
> This plan requires upgrading the Gradle wrapper to **8.9**. I previously encountered a network timeout while downloading Gradle. If it fails again, you may need to manually update the Gradle version in Android Studio's settings or ensure a stable internet connection.

## Proposed Changes

### Build Configuration (Root)

#### [MODIFY] [build.gradle](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/build.gradle)
- Update AGP version to `8.7.3`.

#### [MODIFY] [gradle-wrapper.properties](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/gradle/wrapper/gradle-wrapper.properties)
- Update Gradle version to `8.9`.

#### [MODIFY] [gradle.properties](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/gradle.properties)
- Remove `android.defaults.buildfeatures.buildconfig=true`.

### Module Build Files (BuildConfig Fix)

I will add `buildFeatures { buildConfig = true }` to the following modules:
- [app](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/app/build.gradle)
- [checkbox](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/checkbox/build.gradle)
- [library](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/library/build.gradle)
- [animators](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/animators/build.gradle)
- [cropper](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/cropper/build.gradle)
- [likebutton](file:///E:/Aayush/android/Cliq%20Photohoot%20pose%20cygnux/likebutton/build.gradle)

## Verification Plan

### Automated Tests
- Run `gradlew sync` to verify project health.
- Run `gradlew :app:bundleRelease` from the CLI.

### Manual Verification
- **User Action Required**: Try "Generate Signed Bundle" in Android Studio to confirm the fix for the cross-drive root issue.
