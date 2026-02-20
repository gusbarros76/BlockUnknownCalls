package com.gusbarros.blockcalls.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackground,
    onSurface = OnSurface,
    onError = OnError
)

@Composable
fun BlockUnknownCallsTheme(
    content: @Composable () -> Unit
) {
    // MVP: apenas tema claro
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
