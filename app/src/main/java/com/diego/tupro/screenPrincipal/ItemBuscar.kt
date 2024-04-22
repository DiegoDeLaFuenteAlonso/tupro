package com.diego.tupro.screenPrincipal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemBuscar(navController: NavController) {
    Scaffold (
        // containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            Column {
                BarraSuperior()
            }
        },
        // bottomBar = { BarraInferior(navController = navController, 1)}

    ) { innerPadding ->
        BodyContentBuscar(innerPadding)
    }
}

@Composable
fun BodyContentBuscar(padding: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { /* Haz algo aquí */ }) {
            Text("Buscar")
        }
    }
}
