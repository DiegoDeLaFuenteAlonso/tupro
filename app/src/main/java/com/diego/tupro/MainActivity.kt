package com.diego.tupro

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.diego.prueba.ui.theme.TuproTheme
import com.diego.tupro.navigation.AppNavigation
import com.google.firebase.FirebaseApp

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