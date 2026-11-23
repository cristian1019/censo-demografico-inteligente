package br.edu.unis.censo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.edu.unis.censo.ui.CensoScreen
import br.edu.unis.censo.ui.theme.CensoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CensoTheme {
                CensoScreen()
            }
        }
    }
}
