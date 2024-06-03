package com.diego.tupro.screenPrincipal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.Constantes
import com.diego.tupro.ui.theme.TuproTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ItemInicio(navController: NavController) {
    Scaffold(
        topBar = {
            BarraSuperior("")
        },
        bottomBar = { BarraInferior(navController = navController, 0) }

    ) { innerPadding ->
        BodyContentInicio(innerPadding, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentInicio(padding: PaddingValues, navController: NavController) {
    val isLoading = remember { mutableStateOf(true) }
    val partidos = remember { mutableStateListOf<Partido>() }
    val fecha = remember { mutableStateOf(LocalDate.now()) }

    val openDialogFecha = remember { mutableStateOf(false) }
    val stateFecha = rememberDatePickerState()

    LaunchedEffect (fecha.value){
        isLoading.value = true
        partidos.clear()
        partidos.addAll(getPartidosPorFecha(fecha.value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), partidos))
        isLoading.value = false
    }

    if (openDialogFecha.value){
        DatePickerDialog(
            onDismissRequest = {
                openDialogFecha.value = false
            },
            confirmButton = {
                Button(onClick = {
                    openDialogFecha.value = false
                    stateFecha.selectedDateMillis?.let { millis ->
                        fecha.value = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                }) {
                    Text(text = "Confirmar")
                }
            }
        ) {
            DatePicker(state = stateFecha)
        }
    }

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { fecha.value = fecha.value.minusDays(1) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        // tint = colorScheme.onSecondaryContainer
                    )
                }
                ElevatedButton(
                    onClick = { openDialogFecha.value = true },
                    shape = RoundedCornerShape(Constantes.redondeoBoton)
                ) {
                    Text(
                        text = fecha.value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        fontSize = 20.sp
                    )
                }
                IconButton(onClick = { fecha.value = fecha.value.plusDays(1)}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        // tint = colorScheme.onSecondaryContainer,
                    )
                }

            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
        if(isLoading.value){
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator()
            }
        }
        else if(partidos.isEmpty()){
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text("No hay partidos\npara esta fecha",
                    fontSize = 22.sp,
                    color = colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        else{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                DibujarPartidos(partidos, navController)
            }
        }
    }
}

// Función para obtener partidos por fecha
suspend fun getPartidosPorFecha(fecha: String, partidosExistentes: List<Partido>): List<Partido> {
    val db = Firebase.firestore
    val partidos = mutableListOf<Partido>()

    // Obtiene el array "favCompeticiones" del usuario actual
    val uid = Firebase.auth.currentUser?.uid
    if (uid != null){
        val userDocument = db.collection("users").document(uid ?: "").get().await()
        val favCompeticiones = userDocument.get("favCompeticiones") as? List<String> ?: listOf()
        val favEquipos = userDocument.get("favEquipos") as? List<String> ?: listOf()

        // Realiza una consulta a la colección "partidos"
        val partidosSnapshot = db.collection("partidos")
            .whereEqualTo("fecha", fecha)
            .get()
            .await()

        for (partidoDocument in partidosSnapshot.documents) {
            val idComp = partidoDocument.getString("idComp") ?: "eliminado"
            val idLocal = partidoDocument.getString("idLocal") ?: "eliminado"
            val idVisitante = partidoDocument.getString("idVisitante") ?: "eliminado"

            // Si la competición del partido no está en "favCompeticiones", salta a la siguiente iteración
            if (idComp !in favCompeticiones && idLocal !in favEquipos && idVisitante !in favEquipos) continue

            val idCreador = partidoDocument.getString("creador") ?: ""

            // Realiza consultas a las colecciones "competiciones" y "equipos"
            val competicionDocument = db.collection("competiciones").document(idComp).get().await()
            val localDocument = db.collection("equipos").document(idLocal).get().await()
            val visitanteDocument = db.collection("equipos").document(idVisitante).get().await()
            val creadorDocument = db.collection("users").document(idCreador).get().await()

            // Crea el objeto Partido
            val partido = Partido(
                idPartido = partidoDocument.id,
                competicion = competicionDocument.getString("competicion") ?: "eliminado",
                local = localDocument.getString("equipo") ?: "eliminado",
                visitante = visitanteDocument.getString("equipo") ?: "eliminado",
                fecha = partidoDocument.getString("fecha") ?: "",
                hora = partidoDocument.getString("hora") ?: "",
                estado = partidoDocument.getString("estado") ?: "",
                golesLocal = partidoDocument.getLong("golesLocal").toString(),
                golesVisitante = partidoDocument.getLong("golesVisitante").toString(),
                creador = creadorDocument.getString("username") ?: "eliminado",
                minutos = partidoDocument.getLong("minutos").toString(),
                ganador = partidoDocument.getString("ganador") ?: ""
            )

            // Añade el objeto Partido a la lista solo si no está ya presente
            if (partido !in partidosExistentes) {
                partidos += partido
            }
        }
    }
    return partidos
}


data class Partido(
    val idPartido: String,
    val competicion: String,
    val local: String,
    val visitante: String,
    val fecha: String,
    val hora: String,
    var estado: String,
    val golesLocal: String,
    val golesVisitante: String,
    val creador: String,
    val minutos: String,
    val ganador: String
)

@Preview(showSystemUi = true)
@Composable
fun Greeting() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ItemInicio(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingDark() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ItemInicio(navController)
    }
}