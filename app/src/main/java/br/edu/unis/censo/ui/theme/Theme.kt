package br.edu.unis.censo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Verde = Color(0xFF1B6B4A)
private val Azul = Color(0xFF1D4E89)

@Composable
fun CensoTheme(content: @Composable () -> Unit) {
    val cores = if (isSystemInDarkTheme()) {
        darkColorScheme(primary = Color(0xFF7FD3A8), secondary = Color(0xFF9CC3F5))
    } else {
        lightColorScheme(primary = Verde, secondary = Azul)
    }
    MaterialTheme(colorScheme = cores, content = content)
}
