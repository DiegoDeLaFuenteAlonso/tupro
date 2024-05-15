package com.diego.tupro.screenSecundaria

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@Composable
fun ScreenCrearEquipo(navController: NavController) {
    var textoSnackbar by remember { mutableStateOf("") }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val paddingBotones = 5.dp
    val focusRequester = remember { FocusRequester() }
    var textoCodigo by remember { mutableStateOf("") }
    var textoEquipo by remember { mutableStateOf("") }
    var errorCodigo by remember { mutableStateOf(false) }
    var errorEquipo by remember { mutableStateOf(false) }
    var botonesActivos by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea tu equipo",
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 14.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = textoCodigo,
                onValueChange = {
                    if (it.all { char -> char.isLetterOrDigit() } && it.length <= 3) {
                        textoCodigo = it
                    }
                },
                label = { Text("Abreviatura") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("${textoCodigo.length}/3") },
                isError = errorCodigo,
                trailingIcon = {
                    (if (errorCodigo) Icons.Default.ErrorOutline else null)?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "Mensaje de error"
                        )
                    }
                }

            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = textoEquipo,
                onValueChange = {
                    if (it.all { char -> char.isLetterOrDigit() || char.isWhitespace() } && it.length <= 20) {
                        textoEquipo = it
                    }
                },
                label = { Text("Nombre") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = errorEquipo,
                supportingText = { Text("${textoEquipo.length}/20") },
                trailingIcon = {
                    (if (errorEquipo) Icons.Default.ErrorOutline else null)?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "Mensaje de error"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        botonesActivos = false
                        navController.popBackStack()
                    },
                    enabled = botonesActivos,
                    shape = RoundedCornerShape(Constantes.redondeoBoton),
                    modifier = Modifier
                        .weight(1f)
                        .padding(all = paddingBotones)
                ) {
                    Text(
                        text = "Cancelar",
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = {
                        botonesActivos = false
                        crearEquipo(
                            textoCodigo.trim(),
                            textoEquipo.trim(),
                            { nuevoMensaje -> textoSnackbar = nuevoMensaje },
                            { nuevoMensaje -> errorCodigo = nuevoMensaje },
                            { nuevoMensaje -> errorEquipo = nuevoMensaje },
                            { nuevoMensaje -> botonesActivos = nuevoMensaje},
                            context,
                            navController,
                            softwareKeyboardController
                        )
                    },
                    enabled = botonesActivos,
                    shape = RoundedCornerShape(Constantes.redondeoBoton),
                    modifier = Modifier
                        .weight(1f)
                        .padding(all = paddingBotones)
                ) {
                    Text(
                        text = "Crear",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (textoSnackbar.isNotEmpty()) {
                softwareKeyboardController?.hide()
                Snackbar(
                    action = {
                        TextButton(onClick = { textoSnackbar = "" }) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(textoSnackbar)
                }
                LaunchedEffect(textoSnackbar) {
                    delay(3000L)
                    textoSnackbar = ""
                }
            }
        }
    }
}

fun crearEquipo(
    textoCodigo: String,
    textoEquipo: String,
    actualizarTextoSnackbar: (String) -> Unit,
    actualizarErrorCodigo: (Boolean) -> Unit,
    actualizarErrorEquipo: (Boolean) -> Unit,
    botonesActivos: (Boolean) -> Unit,
    context: Context,
    navController: NavController,
    softwareKeyboardController: SoftwareKeyboardController?
) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    actualizarErrorCodigo(false)
    actualizarErrorEquipo(false)

    if (textoCodigo.isEmpty() || textoEquipo.isEmpty()) {
        botonesActivos(true)
        actualizarTextoSnackbar("Ambos campos son necesarios")
        if (textoCodigo.isEmpty()) actualizarErrorCodigo(true)
        if (textoEquipo.isEmpty()) actualizarErrorEquipo(true)
    } else {
        // Comprueba si el usuario está autenticado
        if (currentUser != null) {
            // Obtén el contador actual
            softwareKeyboardController?.hide()
            val counterRef = db.collection("counters").document("equiposCounter")
            db.runTransaction { transaction ->
                val snapshot = transaction.get(counterRef)
                val newCounter = snapshot.getLong("counter")?.plus(1) ?: 0
                transaction.update(counterRef, "counter", newCounter)

                // Usa el contador como el ID del nuevo documento
                val equipo = hashMapOf(
                    "codigo" to textoCodigo,
                    "equipo" to textoEquipo,
                    "creador" to currentUser.uid,
                    "nombreBusqueda" to textoEquipo.uppercase()
                )
                db.collection("equipos").document(newCounter.toString()).set(equipo)
            }.addOnSuccessListener {
                Log.d("crear_equipo", "Equipo creado con éxito")
                Toast.makeText(context, "Equipo creado con éxito", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }.addOnFailureListener { e ->
                botonesActivos(true)
                Log.w("crear_equipo", "Error al crear el equipo", e)
                actualizarTextoSnackbar("Error al crear el equipo")
            }
        } else {
            botonesActivos(true)
            Log.w("crear_equipo", "Usuario no autenticado")
            actualizarTextoSnackbar("Usuario no autenticado")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewEquipo() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenCrearEquipo(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkEquipo() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenCrearEquipo(navController)
    }
}