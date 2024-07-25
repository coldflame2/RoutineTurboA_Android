import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun themeAwareColor(lightColor: Color, darkColor: Color): Color {
    return if (isSystemInDarkTheme()) darkColor else lightColor
}