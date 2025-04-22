package es.uc3m.android.travelshield.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TurkDark,
    secondary = TurkDark,
    tertiary = TurkDark,
    background = TurkDarkBackground,
    surface = TurkDarkSurface,
    onPrimary = WhiteDark,
    onSecondary = WhiteDark,
    onBackground = WhiteDark,
    onSurface = WhiteDark
)

private val LightColorScheme = lightColorScheme(
    primary = Turk40,
    secondary = TurkWhite40,
    tertiary = TurkBlue40,
    background = TurkLightBackground,
    surface = TurkLightSurface,
    onPrimary = Grey,
    onSecondary = Grey,
    onBackground = Grey,
    onSurface = Grey
)

@Composable
fun TravelShieldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
