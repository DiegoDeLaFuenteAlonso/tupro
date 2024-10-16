package com.diego.tupro

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.diego.tupro.navigation.AppNavigation
import com.diego.tupro.ui.theme.TuproTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            TuproTheme {
                IniciarApp()
            }
        }
    }
}

@Composable
fun IniciarApp() {
    val systemUiController = rememberSystemUiController()
    val temaOscuro = isSystemInDarkTheme()

    val statusBarColor = MaterialTheme.colorScheme.background
    var navigationBarColor = Color(0xFFe7f1e7)
    if (temaOscuro){
        navigationBarColor = Color(0xFF1c2c21)
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = statusBarColor,
            darkIcons = !temaOscuro
        )
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
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