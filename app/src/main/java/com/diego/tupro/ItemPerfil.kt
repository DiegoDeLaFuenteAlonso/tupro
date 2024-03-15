package com.diego.tupro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
fun ItemPerfil(navController: NavController) {
    Scaffold (
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(surface = MaterialTheme.colorScheme.primary, onSurface = MaterialTheme.colorScheme.onPrimary)
            ) {
                Column {
                    TopAppBar(
                        { BarraSuperior() }
                    )
                }
            }
        },
        // bottomBar = { BarraInferior(navController = navController, 2)}

    ) { innerPadding ->
        BodyContentPerfil(innerPadding)
    }
}

@Composable
fun BodyContentPerfil(innerPadding: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { /* Haz algo aquí */ }) {
            Text("Perfil")
        }
    }
}
