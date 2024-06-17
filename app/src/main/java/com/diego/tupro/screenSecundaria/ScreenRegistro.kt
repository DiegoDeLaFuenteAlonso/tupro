package com.diego.tupro.screenSecundaria

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import java.util.regex.Pattern

@Composable
fun ScreenRegistro(navController: NavController) {
    val paddingBotones = 5.dp

    var textoUsuario by remember { mutableStateOf("") }
    var textoCorreo by remember { mutableStateOf("") }
    var textoPass by remember { mutableStateOf("") }
    var textoConfirmPass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    var textoErrorUsuario by remember { mutableStateOf("") }
    var textoErrorCorreo by remember { mutableStateOf("") }
    var textoErrorPass by remember { mutableStateOf("") }
    var textoErrorConfirmPass by remember { mutableStateOf("") }

    var textoSnackbar by remember { mutableStateOf("") }
    var botonesActivos by remember { mutableStateOf(true) }

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val context = LocalContext.current

    val softwareKeyboardController = LocalSoftwareKeyboardController.current


    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrarse",
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 14.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = textoUsuario,
                onValueChange = { if (it.length <= 20) textoUsuario = it },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester1.requestFocus() }),
                modifier = Modifier.fillMaxWidth(),
                isError = textoErrorUsuario.isNotEmpty(),
                supportingText = { Text(textoErrorUsuario) },
                trailingIcon = {
                    (if (textoErrorUsuario.isNotEmpty()) Icons.Default.ErrorOutline else null)?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "Mensaje de error"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = textoCorreo,
                onValueChange = { textoCorreo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester2.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester1),
                isError = textoErrorCorreo.isNotEmpty(),
                supportingText = { Text(textoErrorCorreo) },
                trailingIcon = {
                    (if (textoErrorCorreo.isNotEmpty()) Icons.Default.ErrorOutline else null)?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "Mensaje de error"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = textoPass,
                onValueChange = { textoPass = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequester3.requestFocus() }),
                isError = textoErrorPass.isNotEmpty(),
                supportingText = { Text(textoErrorPass) },
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector = if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester2)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = textoConfirmPass,
                onValueChange = { textoConfirmPass = it },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = textoErrorConfirmPass.isNotEmpty(),
                supportingText = { Text(textoErrorConfirmPass) },
                trailingIcon = {
                    IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                        Icon(
                            imageVector = if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester3)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        botonesActivos = false
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(Constantes.redondeoBoton),
                    enabled = botonesActivos,
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
                        registrarUsuario(
                            textoUsuario.trim(),
                            textoCorreo.trim(),
                            textoPass.trim(),
                            textoConfirmPass.trim(),
                            navController,
                            context,
                            { nuevoMensaje -> textoErrorUsuario = nuevoMensaje },
                            { nuevoMensaje -> textoErrorCorreo = nuevoMensaje },
                            { nuevoMensaje -> textoErrorPass = nuevoMensaje },
                            { nuevoMensaje -> textoErrorConfirmPass = nuevoMensaje },
                            { nuevoMensaje -> textoSnackbar = nuevoMensaje },
                            { nuevoMensaje -> botonesActivos = nuevoMensaje }
                        )
                    },
                    shape = RoundedCornerShape(Constantes.redondeoBoton),
                    enabled = botonesActivos,
                    modifier = Modifier
                        .weight(1f)
                        .padding(all = paddingBotones)
                ) {
                    Text(
                        text = "Registrarse",
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

@SuppressLint("ServiceCast")
fun registrarUsuario(
    textoUsuario: String,
    textoCorreo: String,
    textoPass: String,
    textoConfirmPass: String,
    navController: NavController,
    context: Context,
    actualizarErrorUsuario: (String) -> Unit,
    actualizarErrorCorreo: (String) -> Unit,
    actualizarErrorPass: (String) -> Unit,
    actualizarErrorConfirmPass: (String) -> Unit,
    actualizarTextoSnackbar: (String) -> Unit,
    botonesActivos: (Boolean) -> Unit
) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    actualizarErrorUsuario("")
    actualizarErrorCorreo("")
    actualizarErrorPass("")
    actualizarErrorConfirmPass("")
    actualizarTextoSnackbar("")

    Log.w("registro", "Inicio de registro")
    try {
        // Comprobar la conectividad a Internet
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        val isConnected = activeNetwork?.isConnectedOrConnecting == true

        if (!isConnected) {
            // No hay conexión a Internet
            actualizarTextoSnackbar("No hay conexión a Internet")
            botonesActivos(true)
            return
        }

        // El resto de tu código...
        if (textoUsuario.isEmpty() || textoCorreo.isEmpty() || textoPass.isEmpty() || textoConfirmPass.isEmpty()) {
            Log.d("registro", "Todos los campos deben estar rellenos")
            if (textoUsuario.isEmpty()) {
                actualizarErrorUsuario("Este campo es obligatorio")
            } else if (textoCorreo.isEmpty()) {
                actualizarErrorCorreo("Este campo es obligatorio")
            } else if (textoPass.isEmpty()) {
                actualizarErrorPass("Este campo es obligatorio")
            } else if (textoConfirmPass.isEmpty()) {
                actualizarErrorConfirmPass("Este campo es obligatorio")
            }
        } else if (!isEmailValid(textoCorreo)) {
            Log.d("registro", "Correo no válido")
            actualizarErrorCorreo("Dirección de correo no válida")
        } else if (!isPasswordStrong(textoPass)) {
            Log.d("registro", "contraseña débil")
            actualizarErrorPass("La contraseña es muy debil, debe contener al menos un numero, una mayuscula, una minuscula, no puede tener espacios y debe tener al menos ocho caracteres")
        } else if (textoPass != textoConfirmPass) {
            Log.d("registro", "Las contraseñas no coinciden")
            // Las contraseñas no coinciden
            actualizarErrorConfirmPass("Las contraseñas nos coinciden")
        } else {
            // Comprobar si el nombre de usuario ya está en uso
            db.collection("users").whereEqualTo("username", textoUsuario).get()
                .addOnSuccessListener { result ->
                    if (result.documents.isNotEmpty()) {
                        actualizarErrorUsuario("Usuario existente")
                        Log.w("registro", "documento not null usuario existente")
                    } else {
                        // Comprobar si el correo ya está registrado
                        auth.fetchSignInMethodsForEmail(textoCorreo)
                            .addOnCompleteListener { task ->
                                val isNewUser = task.result?.signInMethods?.isEmpty() ?: true
                                if (isNewUser) {
                                    // Registrar al usuario
                                    auth.createUserWithEmailAndPassword(textoCorreo, textoPass)
                                        .addOnCompleteListener { task2 ->
                                            if (task2.isSuccessful) {
                                                Log.d("registro", "Usuario registrado exitosamente")
                                                // El usuario se registró exitosamente
                                                // Guardar el nombre de usuario en la base de datos
                                                val user = hashMapOf(
                                                    "username" to textoUsuario,
                                                    "usernameBusqueda" to textoUsuario.uppercase(),
                                                    "email" to textoCorreo,
                                                    "favEquipos" to listOf<String>(),
                                                    "favCompeticiones" to listOf<String>()
                                                )
                                                db.collection("users").document(auth.uid!!)
                                                    .set(user)
                                                    .addOnSuccessListener {
                                                        Log.d("registro", "Usuario escrito en el documento con exito")

                                                        // Enviar correo de verificación
                                                        auth.currentUser?.sendEmailVerification()
                                                            ?.addOnCompleteListener { task3 ->
                                                                if (task3.isSuccessful) {
                                                                    Log.d("registro", "Correo de verificación enviado")
                                                                } else {
                                                                    Log.d("registro", "Error al enviar el correo de verificación")
                                                                    navController.popBackStack()
                                                                }
                                                            }
                                                        navController.navigate("screen_verificacion")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w("registro", "Error al escribir el usuario en el documento", e)
                                                        actualizarTextoSnackbar("Error al guardar el usuario")
                                                    }
                                            } else {
                                                Log.d("registro", "Hubo un error al registrar al usuario")
                                                actualizarTextoSnackbar("Error al realizar el registro")
                                            }
                                        }
                                } else {
                                    Log.d("registro", "El correo ya está registrado")
                                    actualizarErrorCorreo("Correo ya resgistrado")
                                }
                            }
                    }
                }.addOnFailureListener { exception ->
                    Log.w("registro", "Error getting document", exception)
                    actualizarTextoSnackbar("Error al comprobar el usuario")
                }
        }
    } catch (e: Exception) {
        Log.w("registro", "Excepcion intento registro: ", e)
        actualizarTextoSnackbar("Ha surgido un error en el intento de registro")
    }
    botonesActivos(true)
}

fun isPasswordStrong(password: String): Boolean {
    val passwordPattern = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +         // al menos 1 dígito
                "(?=.*[a-z])" +         // al menos 1 letra minúscula
                "(?=.*[A-Z])" +         // al menos 1 letra mayúscula
                /*"(?=.*[@#$%^&+=?!])" +*/  // al menos 1 carácter especial
                "(?=\\S+$)" +           // sin espacios en blanco
                ".{8,}" +               // al menos 8 caracteres
                "$"
    )
    val matcher = passwordPattern.matcher(password)
    return matcher.matches()
}

fun isEmailValid(email: String): Boolean {
    val emailPattern =
        ("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z.]{2,}$").toRegex(RegexOption.IGNORE_CASE)
    return email.matches(emailPattern)
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewRegistro() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenRegistro(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkRegistro() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenRegistro(navController)
    }
}
