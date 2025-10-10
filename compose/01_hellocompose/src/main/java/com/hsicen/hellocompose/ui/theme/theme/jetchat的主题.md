
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * 这个是https://github.com/JingMeng/compose-samples/blob/0ae802d9f038b0ae127f78bb698452402e4319e8/Jetchat/app/src/main/java/com/example/compose/jetchat/theme/Themes.kt
 * 里面的，也还是存在类的继承
 */
val JetchatLightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    inversePrimary = Blue80,
    secondary = DarkBlue40,
    onSecondary = Color.White,
    secondaryContainer = DarkBlue90,
    onSecondaryContainer = DarkBlue10,
    tertiary = Yellow40,
    onTertiary = Color.White,
    tertiaryContainer = Yellow90,
    onTertiaryContainer = Yellow10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    surfaceVariant = BlueGrey90,
    onSurfaceVariant = BlueGrey30,
    outline = BlueGrey50,
)
