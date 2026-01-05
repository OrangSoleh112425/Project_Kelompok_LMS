// Top-level build file where you can add configuration options common to all sub-projects/modules.
// gradle.build.kts (Project-Level)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.4" apply false
}