package com.diego.tupro.screenPrincipal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.prueba.ui.theme.TuproTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPerfil(navController: NavController) {
    Scaffold (
        // containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            Column {
                BarraSuperior()

                Row {
                    /*
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Imagen de usuario",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )*/
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.background),
                        contentAlignment = Alignment.Center

                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                    .background(color = colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    //modifier = Modifier.align(Alignment.Start),
                                    text = "    correo     ",
                                    color = colorScheme.onSecondaryContainer,
                                    fontSize = 18.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                    .background(color = colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    //modifier = Modifier.align(Alignment.Start),
                                    text = "    usuario    ",
                                    color = colorScheme.onSecondaryContainer,
                                    fontSize = 18.sp
                                )
                            }
                            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                        }
                    }
                }
            }
        },
        bottomBar = { BarraInferior(navController = navController, 2) }

    ) { innerPadding ->
        BodyContentPerfil(innerPadding)
    }
}

@Composable
fun BodyContentPerfil(innerPadding: PaddingValues) {

}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewPerfil() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ItemPerfil(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkPerfil() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ItemPerfil(navController)
    }
}
