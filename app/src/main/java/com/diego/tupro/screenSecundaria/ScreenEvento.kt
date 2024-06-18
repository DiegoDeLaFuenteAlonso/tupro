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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.Constantes
import com.diego.tupro.ui.theme.TuproTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenEvento(navController: NavController, equipoLocal: String, equipoVisitante: String, idPartido: String, minuto: String) {
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
        BodyContentEvento(it, idPartido, minuto, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BodyContentEvento(
    paddingValues: PaddingValues,
    idPartido: String,
    minutoActual: String,
    navController: NavController
) {
    var minuto by remember { mutableStateOf(minutoActual) }
    var titulo by remember { mutableStateOf("") }
    var texto by remember { mutableStateOf("") }
    var nuevoEvento by remember { mutableStateOf(false) }
    var botonActivo by remember { mutableStateOf(true) }

    val options = listOf("Gol", "Tarjeta", "Cambio")
    var selectedOption by remember { mutableStateOf(options[0]) }

    val optionsEquipo = listOf("Local", "Visitante")
    var selectedOptionEquipo by remember { mutableStateOf(optionsEquipo[0]) }

    val focusManager = LocalFocusManager.current
    val (focusRequester1, focusRequester2, focusRequester3) = remember { FocusRequester.createRefs() }

    LaunchedEffect(nuevoEvento) {
        if (nuevoEvento) {
            crearEvento(idPartido, minuto, titulo, texto, selectedOption.lowercase(), selectedOptionEquipo.lowercase())
            if (selectedOption.lowercase() == "gol") {
                sumarGol(idPartido, selectedOptionEquipo.lowercase() == "local")
            }
            navController.popBackStack()
            nuevoEvento = false
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
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(bottom = 15.dp)
            ) {
                options.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = option == selectedOption,
                        onClick = { selectedOption = option },
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.padding(start = if (index != 0) 5.dp else 0.dp),
                        label = { Text(option) }
                    )
                }
            }

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
                supportingText = {Text(text = "${minuto.length}/3")}
            )

            // Campo de texto Título
            OutlinedTextField(
                value = titulo,
                onValueChange = { if (it.length <= 10) titulo = it },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .focusRequester(focusRequester2),
                keyboardActions = KeyboardActions(onDone = { focusRequester3.requestFocus() }),
                supportingText = {Text(text = "${titulo.length}/10")}
            )

            // Campo de texto Texto
            OutlinedTextField(
                value = texto,
                onValueChange = { if (it.length <= 20) texto = it },
                label = { Text("Texto") },
                singleLine = true,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .focusRequester(focusRequester3),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                supportingText = {Text(text = "${texto.length}/20")}
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(top = 15.dp)
            ) {
                optionsEquipo.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = option == selectedOptionEquipo,
                        onClick = { selectedOptionEquipo = option },
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.padding(start = if (index != 0) 5.dp else 0.dp),
                        label = { Text(option) }
                    )
                }
            }
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
                    nuevoEvento = true
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(Constantes.redondeoBoton),
                enabled = minuto.isNotEmpty() && titulo.isNotEmpty() && texto.isNotEmpty() && botonActivo
            ) {
                Text(text = "Crear evento")
            }
        }
    }
}

suspend fun crearEvento(idPartido: String, minuto: String, titulo: String, texto: String, tipoEvento: String, tipoEquipo: String) {
    if (Firebase.auth.currentUser?.uid != null) {
        val db = Firebase.firestore

        // contador actual
        val counterRef = db.collection("counters").document("equiposCounter")
        val counterSnapshot = counterRef.get().await()
        val currentCounter = counterSnapshot.getLong("counter") ?: 0

        // Incrementar el contador
        counterRef.update("counter", currentCounter + 1)

        // nuevo evento
        val nuevoEvento = hashMapOf(
            "creador" to Firebase.auth.currentUser?.uid,
            "minuto" to minuto.toInt(),
            "titulo" to titulo,
            "texto" to texto,
            "idPartido" to idPartido,
            "tipoEvento" to tipoEvento,
            "tipoEquipo" to tipoEquipo
        )

        // Añadir nuevo evento a la colección "eventos"
        db.collection("eventos").document(currentCounter.toString()).set(nuevoEvento)
    }
}

suspend fun sumarGol(idPartido: String, esLocal: Boolean) {
    var campo ="golesVisitante"
    if (esLocal) {
        campo = "golesLocal"
    }

    val db = Firebase.firestore
    val partidoRef = db.collection("partidos").document(idPartido)

    partidoRef.update(
        campo, FieldValue.increment(1)
    ).await()
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewEvento() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenEvento(navController, "", "", "", "0")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkEvento() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenEvento(navController, "", "", "", "0")
    }
}