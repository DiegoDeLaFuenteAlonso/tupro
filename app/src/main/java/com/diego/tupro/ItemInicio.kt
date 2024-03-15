package com.diego.tupro

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemInicio(navController: NavController) {
    Scaffold (
        containerColor = colorScheme.surfaceVariant,
        // containerColor = androidx.compose.ui.graphics.Color.Black,
        topBar = {
            MaterialTheme(
                // cambiar color top Bar
                colorScheme = colorScheme.copy(surface = colorScheme.primary, onSurface = colorScheme.onPrimary)
            ) {
                Column {
                    TopAppBar(
                        { BarraSuperior() }
                    )
                    Row {
                        FilaInicio()
                    }
                }
            }
        },
        bottomBar = { BarraInferior(navController = navController, 0)}

        ) { innerPadding ->
        BodyContentInicio(innerPadding)
    }
}

@Composable
fun BodyContentInicio(padding: PaddingValues) {
    // Lista de textos para los botones
    // val listaDeBotones = listOf("Botón 1", "Botón 2", "Botón 3", "Botón 4", "Botón 5", "Botón 6", "Botón 7", "Botón 8", "Botón 9")
    val partidos = listOf(
        Partido("Hola", "Hola", "Hola", "30/02/2024", "Hola", null, null),
        Partido("La liga", "Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("Hola", "Hola", "Hola", "30/02/2024", "Hola", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null),
        Partido("La liga", "Real Madrid", "Barcelona", "30/02/2024", "21:00", null, null)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // Itera sobre la lista de textos y crea un botón para cada uno
        items(partidos) { partido ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                shape = RoundedCornerShape(12.dp), // Ajusta este valor a tus necesidades
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Row(
                    Modifier
                        .background(colorScheme.surface)
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 10.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(partido.liga)
                }

                Divider(color = colorScheme.outline, thickness = 1.dp)

                Row(
                    Modifier
                        .background(colorScheme.surface)
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 20.dp, start = 25.dp, end = 25.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        Modifier
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(partido.local, textAlign = TextAlign.Center, fontSize = 20.sp, color = colorScheme.onSurface)
                    }

                    Box(
                        Modifier
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = partido.hora,
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            style = TextStyle(textAlign = TextAlign.Center)
                        )
                    }

                    Box(
                        Modifier
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(partido.visitante, textAlign = TextAlign.Center, fontSize = 20.sp, color = colorScheme.onSurface)
                    }
                    /*
                    Text(partido.local, textAlign = TextAlign.Start)
                    Text(partido.hora, textAlign = TextAlign.Center)
                    Text(partido.visitante, textAlign = TextAlign.End)

                     */
                }
            }
        }
    }
}


@Composable
fun FilaInicio(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Haz algo aquí */ }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = colorScheme.onSecondaryContainer
            )
        }
        Button(onClick = { /* Haz algo aquí */ }) {
            Text("Mi Botón")
        }
        IconButton(onClick = { /* Haz algo aquí */ }) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = colorScheme.onSecondaryContainer,
            )
        }
    }
}

data class Partido(
    val liga: String,
    val local: String,
    val visitante: String,
    val fecha: String,
    val hora: String,
    val estado: String?,
    val marcador: String?
)