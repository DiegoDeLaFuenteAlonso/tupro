package com.diego.tupro.screenPrincipal

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.EmojiEvents
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
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.Constantes
import com.diego.tupro.SessionManager
import com.diego.tupro.ui.theme.TuproTheme
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Locale


@Composable
fun ItemPerfil(navController: NavController) {
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
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val modoEdicion = remember { mutableStateOf(false) }
    val selecEquipos = remember { mutableStateListOf<Equipo>() }
    val selecComp = remember { mutableStateListOf<Comp>() }
    var showDialogEliminar by remember { mutableStateOf(false) }
    val aportaciones = remember { mutableIntStateOf(0) }
    contarAportaciones().addOnSuccessListener { count ->
        aportaciones.intValue = count
    }
    val eliminarRelaciones = remember { mutableStateOf(false) }
    val copiaSelecEquipos = remember { mutableStateListOf<Equipo>() }
    val copiaSelecComp = remember { mutableStateListOf<Comp>() }

    LaunchedEffect(eliminarRelaciones.value) {
        if (eliminarRelaciones.value){
            borrarRelaciones(copiaSelecEquipos, copiaSelecComp)
            eliminarRelaciones.value = false
        }
    }
    val listaEquipos = remember { mutableStateListOf<Equipo>() }
    val isLoadingEquipos = remember { mutableStateOf(true) }
    LaunchedEffect(isLoadingEquipos.value) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (isLoadingEquipos.value && userId != null){
            listaEquipos.clear()
            listaEquipos.addAll(obtenerEquipos(userId))
            isLoadingEquipos.value = false
        }
    }

    val listaComp = remember { mutableStateListOf<Comp>() }
    val isLoadingComp = remember { mutableStateOf(true) }
    LaunchedEffect(isLoadingComp.value) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (isLoadingComp.value && userId != null){
            listaComp.clear()
            listaComp.addAll(obtenerCompeticiones(userId))
            isLoadingComp.value = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                if(modoEdicion.value){
                    TopAppBar(
                        title = { Text(text = "Seleccionados: ${selecEquipos.size + selecComp.size}") },
                        navigationIcon = {
                            IconButton(onClick = {
                                modoEdicion.value = false
                                selecEquipos.clear()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "cancelar")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                showDialogEliminar = true
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "eliminar elementos")
                            }
                        }
                    )
                    if (showDialogEliminar) {
                        AlertDialog(
                            onDismissRequest = { showDialogEliminar = false },
                            title = { Text("Confirmación") },
                            text = { Text("Se van a eliminar ${selecEquipos.size + selecComp.size} elemento(s), ¿deseas continuar?") },
                            confirmButton = {
                                FilledTonalButton(
                                    onClick = {
                                        copiaSelecEquipos.addAll(selecEquipos)
                                        copiaSelecComp.addAll(selecComp)
                                        eliminarRelaciones.value = true
                                        if (selecEquipos.isNotEmpty()) {
                                            val db = FirebaseFirestore.getInstance()
                                            val batch = db.batch()

                                            for (equipo in selecEquipos) {
                                                val docRef = db.collection("equipos").document(equipo.idDocumento)
                                                batch.delete(docRef)
                                            }
                                            // consulta atomica
                                            batch.commit()
                                                .addOnSuccessListener {
                                                    Log.d("eliminar_elementos", "Documentos eliminados con éxito")
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w("eliminar_elementos", "Error al eliminar los documentos", e)
                                                }
                                            selecEquipos.clear()
                                        }
                                        if(selecComp.isNotEmpty()){
                                            val db = FirebaseFirestore.getInstance()
                                            val batch = db.batch()

                                            for (comp in selecComp) {
                                                val docRef = db.collection("competiciones").document(comp.idDocumento)
                                                batch.delete(docRef)
                                            }
                                            batch.commit()
                                                .addOnSuccessListener {
                                                    Log.d("eliminar_elementos", "Documentos eliminados con éxito")
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w("eliminar_elementos", "Error al eliminar los documentos", e)
                                                }
                                            selecComp.clear()
                                        }
                                        contarAportaciones().addOnSuccessListener { count ->
                                            aportaciones.intValue = count
                                        }
                                        isLoadingEquipos.value = true
                                        isLoadingComp.value = true
                                        showDialogEliminar = false
                                    },
                                    colors = ButtonDefaults.filledTonalButtonColors(colorScheme.errorContainer)
                                ) {
                                    Text(
                                        "Eliminar",
                                        textAlign = TextAlign.Center,
                                        color = colorScheme.onErrorContainer
                                    )
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showDialogEliminar = false }) {
                                    Text(
                                        "Cancelar",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        )
                    }

                }
                else if (usuario != "") {
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
                    Row(
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                bottom = 12.dp,
                                start = 10.dp,
                                end = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.2f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(Constantes.redondeoBoton))
                                .background(colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (usuario != null) {
                                Text(
                                    text = usuario.substring(0, 1),
                                    color = colorScheme.onSecondaryContainer,
                                    fontSize = 26.sp
                                )
                            }
                        }
                        Text(text = "Aportaciones: ${aportaciones.intValue}", fontSize = 22.sp, modifier = Modifier.padding(start = 20.dp))
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
            if (usuario != "") {
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
                                navController.navigate("screen_crear_competicion")
                            },
                            text = { Text(text = "Crear competicion") }
                        )
                    }
                }
            }
        },
        bottomBar = { BarraInferior(navController = navController) }

    ) { innerPadding ->
        BodyContentPerfil(
            innerPadding,
            sessionManager,
            selecEquipos,
            modoEdicion,
            selecComp,
            navController,
            listaEquipos,
            isLoadingEquipos,
            listaComp,
            isLoadingComp
        )

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

                    Spacer(modifier = Modifier.height(8.dp))

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
        }
        .addOnFailureListener { e ->
            Log.w("Borrar_Cuenta", "Error al borrar los datos de usuario en Firestore", e)
        }

    // Borrar la cuenta del usuario
    user.delete().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("Borrar_Cuenta", "Cuenta de usuario borrada exitosamente")
        } else {
            Log.d("Borrar_Cuenta", "Error al borrar la cuenta de usuario")
        }
    }

    // Hacer logout al usuario y reiniciar la navegación
    val sessionManager = SessionManager(context)
    sessionManager.logoutUser()
    Constantes.reiniciarNavegacion(navController)
}


fun contarAportaciones(): Task<Int> {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val equiposRef = db.collection("equipos")
    val competicionesRef = db.collection("competiciones")

    val equiposTask = equiposRef.whereEqualTo("creador", auth.uid).get()
    val competicionesTask = competicionesRef.whereEqualTo("creador", auth.uid).get()

    return Tasks.whenAllSuccess<QuerySnapshot>(equiposTask, competicionesTask).continueWith { tasks ->
        var count = 0
        for (task in tasks.result!!) {
            count += task.size()
        }
        count
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BodyContentPerfil(
    innerPadding: PaddingValues,
    sessionManager: SessionManager,
    selecEquipos: SnapshotStateList<Equipo>,
    modoEdicion: MutableState<Boolean>,
    selecComp: SnapshotStateList<Comp>,
    navController: NavController,
    listaEquipos: SnapshotStateList<Equipo>,
    isLoadingEquipos: MutableState<Boolean>,
    listaComp: SnapshotStateList<Comp>,
    isLoadingComp: MutableState<Boolean>
)  {
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    if (currentUser == null) {
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
        var selectedTabIndex by remember { mutableIntStateOf(Constantes.perfilSelectedTabIndex) }
        val tabItems = listOf(
            TabItem("Equipos", Icons.Filled.Shield, Icons.Outlined.Shield),
            TabItem("Competiciones", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
        )
        val pagerState = rememberPagerState {
            tabItems.size
        }
        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
            Constantes.perfilSelectedTabIndex = selectedTabIndex
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
                    .fillMaxSize()
            ) { index ->
                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()
                val user = auth.currentUser

                if (index == 0) {
                    if (user != null) {
                        if (isLoadingEquipos.value) {
                            // Mostrar un indicador de carga mientras la consulta está en progreso
                            Box (
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ){
                                CircularProgressIndicator()
                            }
                        } else if (listaEquipos.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
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
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                items(listaEquipos) { equipo ->
                                    val seleccionado = selecEquipos.contains(equipo)
                                    ListItem(
                                        leadingContent = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(0.2f)
                                                    .aspectRatio(1f)
                                                    .clip(RoundedCornerShape(Constantes.redondeoBoton))
                                                    .background(if (seleccionado) colorScheme.tertiaryContainer else colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center

                                            ) {
                                                Text(
                                                    equipo.codigo.uppercase(Locale.ROOT),
                                                    color = if (seleccionado) colorScheme.onTertiaryContainer else colorScheme.onSecondaryContainer,
                                                    fontSize = 20.sp
                                                )
                                            }
                                        },
                                        headlineContent = { Text(equipo.equipo) },
                                        supportingContent = { Text("#" + equipo.idDocumento) },
                                        trailingContent = { Text(equipo.creador) },
                                        modifier = Modifier.combinedClickable(
                                            onClick = {
                                                if (modoEdicion.value) {
                                                    if (seleccionado) {
                                                        selecEquipos.remove(equipo)
                                                        if(selecComp.isEmpty() && selecEquipos.isEmpty()){
                                                            modoEdicion.value = false
                                                        }
                                                    } else {
                                                        selecEquipos.add(equipo)
                                                    }
                                                } else {navController.navigate("screen_equipo/${equipo.codigo}/${equipo.creador}/${equipo.equipo}/${equipo.idDocumento}")}
                                            },
                                            onLongClick = {
                                                modoEdicion.value = true
                                                selecEquipos.add(equipo)
                                            }
                                        )
                                    )
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                else if(index == 1){
                    if (user != null) {
                        if (isLoadingComp.value) {
                            // Mostrar un indicador de carga mientras la consulta está en progreso
                            Box (
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ){
                                CircularProgressIndicator()
                            }
                        } else if (listaComp.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Aun no has creado\nninguna competición",
                                    fontSize = 22.sp,
                                    color = colorScheme.secondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                items(listaComp) { comp ->
                                    val seleccionado = selecComp.contains(comp)
                                    ListItem(
                                        leadingContent = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(0.2f)
                                                    .aspectRatio(1f)
                                                    .clip(RoundedCornerShape(Constantes.redondeoBoton))
                                                    .background(if (seleccionado) colorScheme.tertiaryContainer else colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center

                                            ) {
                                                Text(
                                                    comp.codigo.uppercase(Locale.ROOT),
                                                    color = if (seleccionado) colorScheme.onTertiaryContainer else colorScheme.onSecondaryContainer,
                                                    fontSize = 20.sp
                                                )
                                            }
                                        },
                                        headlineContent = { Text(comp.nombre) },
                                        supportingContent = { Text("#" + comp.idDocumento) },
                                        trailingContent = { Text(comp.creador) },
                                        modifier = Modifier.combinedClickable(
                                            onClick = {
                                                if (modoEdicion.value) {
                                                    if (seleccionado) {
                                                        selecComp.remove(comp)
                                                        if(selecComp.isEmpty() && selecEquipos.isEmpty()){
                                                            modoEdicion.value = false
                                                        }
                                                    } else {
                                                        selecComp.add(comp)
                                                    }
                                                } else {navController.navigate("screen_competicion/${comp.codigo}/${comp.creador}/${comp.nombre}/${comp.idDocumento}")}
                                            },
                                            onLongClick = {
                                                modoEdicion.value = true
                                                selecComp.add(comp)
                                            }
                                        )
                                    )
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

suspend fun borrarRelaciones(selecEquipos: List<Equipo>, selecComp: List<Comp>) {
    val db = FirebaseFirestore.getInstance()
    val batch = db.batch()

    // Verifica y elimina los equipos y partidos relacionados
    for (equipo in selecEquipos) {
        // Consulta los partidos donde el equipo es local o visitante
        val partidosLocal = db.collection("partidos").whereEqualTo("idLocal", equipo.idDocumento).get().await()
        val partidosVisitante = db.collection("partidos").whereEqualTo("idVisitante", equipo.idDocumento).get().await()

        // Añade los documentos de los partidos a la operación de eliminación por lotes
        for (partidoDocument in partidosLocal) {
            batch.delete(partidoDocument.reference)
        }
        for (partidoDocument in partidosVisitante) {
            batch.delete(partidoDocument.reference)
        }
    }

    // Verifica y elimina las competiciones y partidos relacionados
    for (comp in selecComp) {
        // Consulta los partidos donde la competición es la especificada
        val partidosComp = db.collection("partidos").whereEqualTo("idComp", comp.idDocumento).get().await()

        // Añade los documentos de los partidos a la operación de eliminación por lotes
        for (partidoDocument in partidosComp) {
            batch.delete(partidoDocument.reference)
        }
    }

    // Consulta atómica
    batch.commit()
        .addOnSuccessListener {
            Log.d("eliminar_elementos", "Documentos eliminados con éxito")
        }
        .addOnFailureListener { e ->
            Log.w("eliminar_elementos", "Error al eliminar los documentos", e)
        }
}

suspend fun obtenerEquipos(userId: String): List<Equipo> {
    val db = FirebaseFirestore.getInstance()
    val listaEquipos = mutableListOf<Equipo>()
    val snapshot = db.collection("equipos")
        .whereEqualTo("creador", userId)
        .get()
        .await()

    if (!snapshot.isEmpty) {
        for (document in snapshot.documents) {
            val codigo = document.getString("codigo") ?: ""
            val equipo = document.getString("equipo") ?: ""
            val idDocumento = document.id
            val creadorId = document.getString("creador") ?: ""

            val userDocument = db.collection("users").document(creadorId)
                .get()
                .await()

            val username = userDocument.getString("username") ?: ""
            listaEquipos.add(Equipo(codigo, equipo, idDocumento, username))
        }
    }

    return listaEquipos
}

suspend fun obtenerCompeticiones(userId: String): List<Comp> {
    val db = FirebaseFirestore.getInstance()
    val listaComp = mutableListOf<Comp>()
    val snapshot = db.collection("competiciones")
        .whereEqualTo("creador", userId)
        .get()
        .await()

    if (!snapshot.isEmpty) {
        for (document in snapshot.documents) {
            val equipos = document.get("equipos") as List<*>
            val equiposExistentes = mutableListOf<String>()
            val tasks = mutableListOf<Task<DocumentSnapshot>>()

            equipos.forEach { idEquipo ->
                val equipoRef = db.collection("equipos").document(idEquipo.toString())
                val task = equipoRef.get()
                tasks.add(task)
                task.addOnSuccessListener { equipoDocument ->
                    if (equipoDocument.exists()) {
                        equiposExistentes.add(idEquipo.toString())
                    }
                }
            }

            Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener {
                if (equipos.size != equiposExistentes.size) {
                    document.reference.update("equipos", equiposExistentes)
                }
            }

            val codigo = document.getString("codigo") ?: ""
            val comp = document.getString("competicion") ?: ""
            val idDocumento = document.id
            val creadorId = document.getString("creador") ?: ""

            val userDocument = db.collection("users").document(creadorId)
                .get()
                .await()

            val username = userDocument.getString("username") ?: ""
            listaComp.add(Comp(codigo, comp, idDocumento, username))
        }
    }
    return listaComp
}

data class Equipo(
    val codigo: String,
    val equipo: String,
    val idDocumento: String,
    val creador: String
)

data class Comp(
    val codigo: String,
    val nombre: String,
    val idDocumento: String,
    val creador: String
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
