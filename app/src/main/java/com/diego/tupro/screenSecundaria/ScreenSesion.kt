package com.diego.tupro.screenSecundaria

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
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
import com.diego.prueba.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.content.Context
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Snackbar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.diego.tupro.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay


@Composable
fun ScreenSesion(navController: NavController) {
    val paddingBotones = 5.dp
    var textoCorreo by remember { mutableStateOf("") }
    var textoPass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    var textoErrorCorreo by remember { mutableStateOf("") }
    var textoErrorPass by remember { mutableStateOf("") }

    var textoSnackbar by remember { mutableStateOf("") }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar sesión",
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 14.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = textoCorreo,
                onValueChange = { textoCorreo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
                modifier = Modifier.fillMaxWidth(),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "¿No tienes una cuenta? Regístrate",
                color = colorScheme.secondary,
                modifier = Modifier
                    .clickable { navController.navigate("screen_registro") }
                    .padding(top = 10.dp, bottom = 8.dp)
            )

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Text(
                text = "Recupera tu contraseña",
                color = colorScheme.secondary,
                modifier = Modifier
                    .clickable {
                        cambiarPass(
                            textoCorreo,
                            { nuevoMensaje -> textoErrorCorreo = nuevoMensaje },
                            { nuevoMensaje -> textoSnackbar = nuevoMensaje}
                        ) }
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
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
                        singIn(
                            textoCorreo.trim(),
                            textoPass.trim(),
                            navController,
                            context,
                            { nuevoMensaje -> textoErrorCorreo = nuevoMensaje },
                            { nuevoMensaje -> textoErrorPass = nuevoMensaje },
                            { nuevoMensaje -> textoSnackbar = nuevoMensaje}
                        )
                    },
                    shape = RoundedCornerShape(Constantes.redondeoBoton),
                    modifier = Modifier
                        .weight(1f)
                        .padding(all = paddingBotones)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ){
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

fun cambiarPass(
    textoCorreo: String,
    actualizarErrorCorreo: (String) -> Unit,
    actualizarTextoSnackbar: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    actualizarErrorCorreo("")
    actualizarTextoSnackbar("")

    if(textoCorreo.isEmpty()){
        actualizarErrorCorreo("Ingresa una dirección de correo para recuperar tu contraseña")
    }
    else if(!isEmailValid(textoCorreo)){
        actualizarErrorCorreo("Ingresa una dirección de correo válida")
    }
    else{
        auth.sendPasswordResetEmail(textoCorreo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("cambio_pass", "Correo recuperación enviado correctamente")
                } else {
                    Log.d("cambio_pass", "Error envio correo recuperación")
                }
                actualizarTextoSnackbar("Correo de recuperación enviado con éxito")
            }
            .addOnFailureListener { exception ->
                Log.d("canbio_pass", "Error tarea enviar correo recuperacion ", exception)
            }
    }
}

fun singIn(
    textoCorreo: String,
    textoPass: String,
    navController: NavController,
    context: Context,
    actualizarErrorCorreo: (String) -> Unit,
    actualizarErrorPass: (String) -> Unit,
    actualizarTextoSnackbar: (String) -> Unit
) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    actualizarErrorCorreo("")
    actualizarErrorPass("")
    actualizarTextoSnackbar("")

    Log.d("Inicio_Sesion", "Comienzo inicio sesion")
    try {
        if (textoCorreo.isEmpty() || textoPass.isEmpty()) {
            Log.d("Inicio_Sesion", "Campos de inicio sesion vacios")
            if (textoCorreo.isEmpty()){
                actualizarErrorCorreo("Este campo es obligatorio")
            } else if (textoPass.isEmpty()){
                actualizarErrorPass("Este campo es obligatorio")
            }
        } else {
            // Comprobar si el correo existe en la base de datos
            auth.fetchSignInMethodsForEmail(textoCorreo)
                .addOnCompleteListener { task ->
                    val isNewUser = task.result?.signInMethods?.isEmpty() ?: true
                    if (!isNewUser) {
                        // El correo existe, intentar iniciar sesión
                        auth.signInWithEmailAndPassword(textoCorreo, textoPass)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    // Iniciar sesión exitosamente
                                    val user = auth.currentUser
                                    if (user?.isEmailVerified == true) {
                                        // El correo está verificado
                                        Log.d("Inicio_Sesion", "Inicio de sesion correcto")
                                        Constantes.reiniciarNavegacion(navController)
                                        val sessionManager = SessionManager(context)

                                        // Obtener el nombre de usuario desde Firestore
                                        db.collection("users").document(auth.uid!!)
                                            .get()
                                            .addOnSuccessListener { document ->
                                                if (document != null) {
                                                    Log.d(
                                                        "Firestore",
                                                        "DocumentSnapshot data: ${document.data}"
                                                    )
                                                    val username = document.getString("username")
                                                    if (username != null) {
                                                        sessionManager.saveUserDetails(
                                                            textoCorreo,
                                                            username
                                                        )
                                                    }
                                                } else {
                                                    Log.d("Firestore", "No such document")
                                                }
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.d("Firestore", "get failed with ", exception)
                                                actualizarTextoSnackbar("Error al intentar iniciar sesión")
                                            }
                                    } else {
                                        // El correo no está verificado
                                        user?.sendEmailVerification()
                                            ?.addOnCompleteListener { task3 ->
                                                if (task3.isSuccessful) {
                                                    Log.d(
                                                        "Inicio_Sesion",
                                                        "Correo de verificación enviado"
                                                    )
                                                } else {
                                                    Log.d(
                                                        "Inicio_Sesion",
                                                        "Error al enviar el correo de verificación"
                                                    )
                                                    // TODO: Manejar este error
                                                }
                                            }
                                        navController.navigate("screen_verificacion")
                                    }
                                } else {
                                    Log.w(
                                        "Inicio_Sesion",
                                        "Falló de credenciales al intentar iniciar sesión"
                                    )
                                    actualizarErrorCorreo("Correo o contraseña incorrectos")
                                    /*
                                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                        // La contraseña no es válida
                                        actualizarErrorCorreo("Correo o contraseña incorrectos")
                                    }*/
                                }
                            }
                    } else {
                        Log.d("Inicio_Sesion", "El correo no está registrado")
                        actualizarErrorCorreo("Correo o contraseña incorrectos")
                    }
                }
        }
    } catch (e: Exception) {
        Log.w("Inicio_Sesion", "Excepcion en inicio sesion: ", e)
        actualizarTextoSnackbar("Error al iniciar sesión")
    }
}

/*
@Composable
fun GuardarSesion() {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)
    val user = com.google.firebase.ktx.Firebase.auth.currentUser

    user?.let {
        // Obtener los datos del usuario de Firestore
        db.collection("usernames").document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Inicio_Sesion", "Documento encontrado: ${document.data}")
                    val username = document.getString("username")
                    val email = document.getString("email")

                    // Guardar los datos del usuario en las preferencias compartidas
                    with(sharedPref.edit()) {
                        putString("username", username)
                        putString("email", email)
                        putBoolean("sesionIniciada", true)
                        commit()
                    }

                } else {
                    Log.d("Inicio_Sesion", "No existe codumento con nombre: ${user.uid}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Inicio_Sesion", "Error guardar datos inicio de sesion: ", exception)
            }
    }
}
*/
@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewSesion() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenSesion(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkSesion() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenSesion(navController)
    }
}