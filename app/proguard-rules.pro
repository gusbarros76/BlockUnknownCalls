# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Koin
-keep class org.koin.** { *; }
-keep class kotlin.Metadata { *; }

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
