package com.diego.tupro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.diego.tupro.ui.theme.TuproTheme
import com.diego.tupro.navigation.AppNavigation
import com.google.accompanist.systemuicontroller.rememberSystemUiController

enum class TipoProveedor{
    BASIC,
    GOOGLE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TuproTheme {
                // A surface container using the 'background' color from the theme
                // FirebaseApp.initializeApp(applicationContext)
                IniciarApp()
            }
        }
    }
}

@Composable
fun IniciarApp() {
    val systemUiController = rememberSystemUiController()
    val temaOscuro = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !temaOscuro
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !temaOscuro
        )
    }
    AppNavigation()
}

@Preview(showSystemUi = true)
@Composable  
fun GreetingPreview() {
    TuproTheme(darkTheme = false) {
        IniciarApp()
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDark() {
    TuproTheme(darkTheme = true) {
        IniciarApp()
    }
}