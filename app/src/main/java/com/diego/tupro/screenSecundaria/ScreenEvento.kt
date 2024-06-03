package com.diego.tupro.screenSecundaria

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ui.theme.TuproTheme

@Composable
fun ScreenEvento(navController: NavController){
    Scaffold (

    ) {
        BodyContentEvento(it)
    }
}

@Composable
fun BodyContentEvento(paddingValues: PaddingValues) {

}


@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewEvento() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenEvento(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkEvento() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenEvento(navController)
    }
}