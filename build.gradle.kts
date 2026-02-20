plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
}

detekt {
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
    source.setFrom(
        "app/src/main/java",
        "app/src/test/java"
    )
}
