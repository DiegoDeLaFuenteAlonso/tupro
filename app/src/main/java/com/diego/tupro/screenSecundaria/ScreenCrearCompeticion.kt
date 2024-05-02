package com.diego.tupro.screenSecundaria

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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.prueba.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import kotlinx.coroutines.delay

@Composable
fun ScreenCrearCompeticion(navController: NavController) {
    var textoSnackbar by remember { mutableStateOf("") }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val paddingBotones = 5.dp
    val focusRequester = remember { FocusRequester() }
    var textoComp by remember { mutableStateOf("") }
    var errorComp by remember { mutableStateOf(false) }
    var botonesActivos by remember { mutableStateOf(true) }
    var textoCodigo by remember { mutableStateOf("") }
    var errorCodigo by remember { mutableStateOf(false) }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea tu competición",
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
                value = textoComp,
                onValueChange = {
                    if (it.all { char -> char.isLetterOrDigit() || char.isWhitespace() } && it.length <= 20) {
                        textoComp = it
                    }
                },
                label = { Text("Nombre") },
                singleLine = true,
                isError = errorComp,
                supportingText = { Text("${textoComp.length}/30") },
                trailingIcon = {
                    (if (errorComp) Icons.Default.ErrorOutline else null)?.let {
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
                        if (textoComp.trim().isEmpty() || textoCodigo.trim().isEmpty()) {
                            errorComp = true
                            errorCodigo = true
                            botonesActivos = true
                            textoSnackbar = "Ningún campo puede estar vacío"
                        }
                        else { navController.navigate("screen_busqueda_equipos/" + textoComp.trim() + "/" + textoCodigo.trim()) }
                    },
                    enabled = botonesActivos,
                    shape = RoundedCornerShape(Constantes.redondeoBoton),
                    modifier = Modifier
                        .weight(1f)
                        .padding(all = paddingBotones)
                ) {
                    Text(
                        text = "Siguiente",
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

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewCompeticion() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenCrearCompeticion(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkCompeticion() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenCrearCompeticion(navController)
    }
}