package com.diego.tupro.screenSecundaria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.diego.tupro.Constantes
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenEditarMarcador(navController: NavController, equipoLocal: String, equipoVisitante: String, idPartido: String, minuto: String, golesLocal: String, golesVisitante: String, enJuego: Boolean, finalizado: Boolean) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("$equipoLocal - $equipoVisitante") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "atrás")
                        }
                    }
                )
                HorizontalDivider(thickness = 1.dp)
            }
        }
    ) {
        BodyContentEditarMarcador(it, idPartido, minuto, golesLocal, golesVisitante, enJuego, navController, finalizado)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BodyContentEditarMarcador(
    paddingValues: PaddingValues,
    idPartido: String,
    minutoActual: String,
    golesLocal: String,
    golesVisitante: String,
    enJuego: Boolean,
    navController: NavController,
    finalizado: Boolean
) {
    var minuto by remember { mutableStateOf(minutoActual) }
    var golesL by remember { mutableStateOf(golesLocal) }
    var golesV by remember { mutableStateOf(golesVisitante) }

    var cambiarMarcador by remember { mutableStateOf(false) }
    var botonActivo by remember { mutableStateOf(true) }

    val focusManager = LocalFocusManager.current
    val (focusRequester1, focusRequester2, focusRequester3) = remember { FocusRequester.createRefs() }

    LaunchedEffect(cambiarMarcador) {
        if (cambiarMarcador) {
            actualizarMarcador(idPartido, minuto, golesL, golesV)
            if (finalizado) {
                val ganador = if (golesL > golesV) "local" else if (golesL < golesV) "visitante" else "empate"
                funActualizarEstadoPartido(idPartido, "finalizado", ganador)
            }
            navController.popBackStack()
            cambiarMarcador = false
            botonActivo = true
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column (
            modifier = Modifier
                .weight(0.9f)
                .fillMaxWidth()
                .padding(top = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // Campo de texto Minuto
            OutlinedTextField(
                value = minuto,
                onValueChange = { if (it.length <= 3 && it.all { it.isDigit() }) minuto = it },
                label = { Text("Minuto") },
                singleLine = true,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .focusRequester(focusRequester1),
                keyboardActions = KeyboardActions(onDone = { focusRequester2.requestFocus() }),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {Text(text = "${minuto.length}/3")},
                enabled = enJuego
            )

            // Campo de texto goles local
            OutlinedTextField(
                value = golesL,
                onValueChange = { if (it.length <= 3 && it.all { it.isDigit() }) golesL = it },
                label = { Text("Goles local") },
                singleLine = true,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .focusRequester(focusRequester2),
                keyboardActions = KeyboardActions(onDone = { focusRequester3.requestFocus() }),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {Text(text = "${golesL.length}/3")}
            )

            // Campo de texto goles visitante
            OutlinedTextField(
                value = golesV,
                onValueChange = { if (it.length <= 3 && it.all { it.isDigit() }) golesV = it },
                label = { Text("Goles visitante") },
                singleLine = true,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .focusRequester(focusRequester3),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {Text(text = "${golesV.length}/3")}
            )

        }

        HorizontalDivider(thickness = 1.dp)
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .padding(start = 20.dp, top = 5.dp, end = 20.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    botonActivo = false
                    cambiarMarcador = true
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(Constantes.redondeoBoton),
                enabled = minuto.isNotEmpty() && golesL.isNotEmpty() && golesV.isNotEmpty() && botonActivo
            ) {
                Text(text = "Editar marcador")
            }
        }
    }
}

suspend fun actualizarMarcador(idPartido: String, minuto: String, golesL: String, golesV: String) {
    val db = Firebase.firestore
    val partidoRef = db.collection("partidos").document(idPartido)

    partidoRef.update(
        "minutos", minuto.toInt(),
        "golesLocal", golesL.toInt(),
        "golesVisitante", golesV.toInt()
    ).await()
}
