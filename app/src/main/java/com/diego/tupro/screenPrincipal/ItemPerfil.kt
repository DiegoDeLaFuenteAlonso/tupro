package com.diego.tupro.screenPrincipal

import android.app.LauncherActivity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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
                                    /*
                                    ElevatedButton(
                                        onClick = { /*TODO*/ },
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
                                    }*/
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
        floatingActionButton = {
            var showMenu by remember { mutableStateOf(false) }

            Box {
                FloatingActionButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Filled.Add, contentDescription = "Crear equipo / competicion")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            navController.navigate("screen_crear_equipo")
                        },
                        text = { Text(text = "Crear equipo") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                        },
                        text = { Text(text = "Crear competicion") }
                    )
                }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BodyContentPerfil(innerPadding: PaddingValues) {
    //val auth = Firebase.auth
    //val currentUser = auth.currentUser

    if ("currentUser" == null) {
        // El usuario no está registrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Accede con tu cuenta\npara empezar a crear equipos",
                fontSize = 22.sp,
                color = colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    } else {
        // El usuario está registrado
        if (1 == 2) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Aun no has creado\nningún equipo",
                    fontSize = 22.sp,
                    color = colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            var selectedTabIndex by remember { mutableStateOf(0) }
            val tabItems = listOf(
                TabItem("Equipos", Icons.Filled.Shield, Icons.Outlined.Shield),
                TabItem("Competiciones", Icons.Filled.Groups, Icons.Outlined.Groups)
            )
            val pagerState = rememberPagerState {
                tabItems.size
            }
            LaunchedEffect(selectedTabIndex) {
                pagerState.animateScrollToPage(selectedTabIndex)
            }
            LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                if (!pagerState.isScrollInProgress) {
                    selectedTabIndex = pagerState.currentPage
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex
                ) {
                    tabItems.forEachIndexed { index, item ->
                        Tab(
                            selected = (selectedTabIndex == index),
                            onClick = {
                                selectedTabIndex = index
                            },
                            modifier = Modifier
                                .padding(
                                    top = 6.dp,
                                    bottom = 6.dp
                                )
                        ) {
                            Icon(
                                imageVector = if (index == selectedTabIndex) item.selecIcon else item.unselecIcon,
                                contentDescription = item.titulo
                            )
                            Text(text = item.titulo)
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val auth = FirebaseAuth.getInstance()
                        val db = FirebaseFirestore.getInstance()
                        val user = auth.currentUser

                        if (index == 0) {
                            if (user != null) {
                                val auth = FirebaseAuth.getInstance()
                                val db = FirebaseFirestore.getInstance()
                                val user = auth.currentUser

                                // Crear un estado mutable para la lista de equipos
                                val listaEquipos = remember { mutableStateListOf<Equipo>() }

                                // Crear un estado mutable para el estado de carga
                                val isLoading = remember { mutableStateOf(true) }

                                LaunchedEffect(key1 = user) {
                                    if (user != null) {
                                        db.collection("equipos")
                                            .whereEqualTo("creador", user.uid)
                                            .get()
                                            .addOnSuccessListener { result ->
                                                for (document in result) {
                                                    val codigo = document.getString("codigo") ?: ""
                                                    val equipo = document.getString("equipo") ?: ""
                                                    val idDocumento = document.id
                                                    listaEquipos.add(Equipo(codigo, equipo, idDocumento))
                                                }
                                                // Actualizar el estado de carga cuando la consulta haya terminado
                                                isLoading.value = false
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.w("TAG", "Error getting documents: ", exception)
                                            }
                                    }
                                }

                                // Mostrar la lista de equipos o un mensaje, dependiendo del estado de carga
                                if (isLoading.value) {
                                    // Mostrar un indicador de carga mientras la consulta está en progreso
                                    LinearProgressIndicator()
                                } else if (listaEquipos.isEmpty()) {
                                    Text("Aun no has creado ningún equipo")
                                } else {
                                    LazyColumn {
                                        items(listaEquipos) { equipo ->
                                            Text(text = "${equipo.equipo}, ${equipo.codigo}, ${equipo.idDocumento}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Equipo(
    val codigo: String,
    val equipo: String,
    val idDocumento: String
)

data class TabItem(
    val titulo: String,
    val selecIcon: ImageVector,
    val unselecIcon: ImageVector
)

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
