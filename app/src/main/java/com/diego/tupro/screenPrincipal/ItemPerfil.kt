package com.diego.tupro.screenPrincipal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.prueba.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.diego.tupro.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPerfil(navController: NavController) {
    // val userSession by userViewModel.userSession.observeAsState()
    EstructuraItemPerfil(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstructuraItemPerfil(navController: NavController) {
    val paddingBotones = 5.dp
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    val sessionManager = SessionManager(LocalContext.current)
    val user = sessionManager.getUserDetails()
    val usuario = user["username"]
    val correo = user["email"]
    // Estado para mostrar/ocultar el diálogo de confirmación
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val signInResultLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task, navController, activity)
            }
            Activity.RESULT_CANCELED -> {
                // El usuario canceló el intento de inicio de sesión
                Log.d("TAG", "Sign in cancelled")
            }
            else -> {
                // Ocurrió un error
                Log.d("TAG", "Sign in failed with result code: ${result.resultCode}")
            }
        }
    }



    Scaffold(
        topBar = {
            Column {
                if (usuario != "") {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (usuario != null) {
                                    Text(
                                        text = usuario,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary,
                                        modifier = Modifier.clickable { isSheetOpen = true }
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = "Desplegable",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.clickable { isSheetOpen = true }
                                )
                            }
                        }
                    )
                    HorizontalDivider(thickness = 1.dp)
                    Row {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            // El usuario ha iniciado sesión
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 20.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                        .background(
                                            color = colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        text = "\tCorreo: $correo"/*${userSession!!.email}*/,
                                        color = colorScheme.onSecondaryContainer,
                                        fontSize = 18.sp
                                    )
                                }/*
                                Box(
                                    modifier = Modifier
                                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                        .background(
                                            color = colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        text = "Usuario: " /*${userSession!!.displayName}*/,
                                        color = colorScheme.onSecondaryContainer,
                                        fontSize = 18.sp
                                    )
                                }*/
                            }
                        }
                    }
                } else {
                    BarraSuperior("")
                    Row {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            // El usuario no ha iniciado sesión
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 20.dp)
                            ) {
                                Text(text = "Acceder:")
                                Row {
                                    ElevatedButton(
                                        onClick = { navController.navigate("screen_sesion") },
                                        Modifier
                                            .weight(1f)
                                            .padding(
                                                start = paddingBotones,
                                                end = paddingBotones,
                                                top = paddingBotones
                                            ),
                                        shape = RoundedCornerShape(Constantes.redondeoBoton)
                                    ) {
                                        Text(
                                            text = "Correo electrónico",
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    ElevatedButton(
                                        onClick = { signInWithGoogle(signInResultLauncher, navController, context) },
                                        Modifier
                                            .weight(1f)
                                            .padding(
                                                start = paddingBotones,
                                                end = paddingBotones,
                                                top = paddingBotones
                                            ),
                                        shape = RoundedCornerShape(Constantes.redondeoBoton)
                                    ) {
                                        Text(text = "Google", textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )
            }
        },
        bottomBar = { BarraInferior(navController = navController, 2) }

    ) { innerPadding ->
        BodyContentPerfil(innerPadding)

        val sheetState = rememberModalBottomSheetState()

        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    isSheetOpen = false
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FilledTonalButton(
                        onClick = {
                            isSheetOpen = false
                            cerrarSesion(navController, context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Constantes.redondeoBoton)
                    ) {
                        Text(text = "Cerrar Sesion", textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.height(8.dp)) // Espacio entre los botones

                    FilledTonalButton(
                        onClick = {
                            showDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.filledTonalButtonColors(colorScheme.errorContainer),
                        shape = RoundedCornerShape(Constantes.redondeoBoton)
                    ) {
                        Text(
                            text = "Eliminar cuenta",
                            textAlign = TextAlign.Center,
                            color = colorScheme.onErrorContainer
                        )
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Confirmación") },
                            text = { Text("¿Estás seguro de que quieres eliminar tu cuenta?") },
                            confirmButton = {
                                Button(onClick = {
                                    eliminarCuenta(navController, context)
                                    showDialog = false
                                }) {
                                    Text("Continuar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showDialog = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp)) // Margen adicional al final
                }
            }

        }
    }
}

fun signInWithGoogle(
    resultLauncher: ActivityResultLauncher<Intent>,
    navController: NavController,
    context: Context
) {
    // Crear una instancia de GoogleSignInClient
    val DEFAULT_WEB_CLIENT_ID = "707739853008-2sfljn1vebcrggb96s5fupmonl3d8pil.apps.googleusercontent.com"
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(DEFAULT_WEB_CLIENT_ID)
        .requestEmail()
        .build()

    val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

    // Iniciar el proceso de autenticación con Google
    val signInIntent = mGoogleSignInClient.signInIntent
    resultLauncher.launch(signInIntent)
}

fun handleSignInResult(
    task: Task<GoogleSignInAccount>,
    navController: NavController,
    activity: Activity?
) {
    try {
        // Google Sign In fue exitoso, autenticar con Firebase
        val account = task.getResult(ApiException::class.java)!!
        if (activity != null) {
            firebaseAuthWithGoogle(account.idToken!!, navController, activity)
        }
        else{
            Log.w("TAG", "activity es null")
        }
    } catch (e: ApiException) {
        // Google Sign In falló, actualizar UI apropiadamente
        Log.w("TAG", "Google sign in failed", e)
    }
}

private fun firebaseAuthWithGoogle(idToken: String, navController: NavController, activity: Activity) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val auth = Firebase.auth
    auth.signInWithCredential(credential)
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                // Inicio de sesión exitoso, actualizar UI con la información del usuario de Firebase
                val user = auth.currentUser
                updateUI(user, navController)
            } else {
                // Si el inicio de sesión falla, mostrar un mensaje al usuario.
                Log.w("TAG", "signInWithCredential:failure", task.exception)
                updateUI(null, navController)
            }
        }
}

fun updateUI(user: FirebaseUser?, navController: NavController) {
    if (user != null) {
        // El usuario se autenticó con éxito, puedes navegar a otra pantalla aquí
        // o actualizar la interfaz de usuario de alguna otra manera
        Constantes.reiniciarNavegacion(navController)
        println("Usuario autenticado: ${user.displayName}")
    } else {
        // El inicio de sesión falló, muestra un mensaje al usuario
        println("Error en el inicio de sesión")
    }
}


fun cerrarSesion(navController: NavController, context: Context) {
    val auth = Firebase.auth
    auth.signOut()
    val sessionManager = SessionManager(context)
    sessionManager.logoutUser()
    Constantes.reiniciarNavegacion(navController)
}

fun eliminarCuenta(navController: NavController, context: Context) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val user = auth.currentUser

    // Borrar los datos del usuario en Firestore
    db.collection("users").document(user?.uid!!)
        .delete()
        .addOnSuccessListener {
            Log.d("Borrar_Cuenta", "Datos de usuario borrados exitosamente de Firestore")

            // Borrar la cuenta del usuario
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Borrar_Cuenta", "Cuenta de usuario borrada exitosamente")
                    val sessionManager = SessionManager(context)
                    sessionManager.logoutUser()
                    Constantes.reiniciarNavegacion(navController)
                } else {
                    Log.d("Borrar_Cuenta", "Error al borrar la cuenta de usuario")
                    // TODO: Manejar este error
                }
            }
        }
        .addOnFailureListener { e ->
            Log.w("Borrar_Cuenta", "Error al borrar los datos de usuario en Firestore", e)
            // TODO: Manejar este error
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentPerfil(innerPadding: PaddingValues) {

}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewPerfil() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        // val userViewModel: UserViewModel = viewModel()
        ItemPerfil(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkPerfil() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        // val userViewModel: UserViewModel = viewModel()
        ItemPerfil(navController)
    }
}
