package com.diego.tupro.screenSecundaria

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.twotone.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.diego.tupro.SessionManager
import com.diego.tupro.screenPrincipal.Comp
import com.diego.tupro.screenPrincipal.DibujarPartidos
import com.diego.tupro.screenPrincipal.Partido
import com.diego.tupro.screenPrincipal.TabItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenCompeticion(
    navController: NavController,
    codigo: String,
    creador: String,
    competicion: String,
    id: String
) {
    val comp = Comp(codigo, competicion, id, creador)
    val esFav = remember { mutableStateOf(false) }
    val isLoadingInterfaz = remember { mutableStateOf(true) }
    val actualizarFav = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val sessionManager = SessionManager(LocalContext.current)
    val user = sessionManager.getUserDetails()

    LaunchedEffect(Unit) {
        limpiarEquiposBorrados(comp.idDocumento)
        esFav.value = existeFavComp(comp.idDocumento, uid)
        isLoadingInterfaz.value = false
    }
    LaunchedEffect(actualizarFav.value) {
        if (actualizarFav.value) {
            actualizarFavComp(comp.idDocumento, uid)
            actualizarFav.value = false
            esFav.value = !esFav.value
        }
    }
    if(isLoadingInterfaz.value){
        Box (
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ){
            CircularProgressIndicator()
        }
    } else{
        Scaffold(
            topBar = {
                Column{
                    TopAppBar(
                        title = {
                            Text(text = "Competicion", color = colorScheme.primary, fontSize = 26.sp)
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { navController.popBackStack() }
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { if(uid != null) actualizarFav.value = true else showDialog.value = true}
                            ) {
                                Icon(
                                    if (esFav.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Añadir a favoritos"
                                )
                            }
                        }
                    )
                    HorizontalDivider()
                }
            },
            floatingActionButton = {
                if (user["username"] == comp.creador) {
                    var showMenu by remember { mutableStateOf(false) }

                    Box {
                        FloatingActionButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Filled.Add, contentDescription = "Crear partido")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    showMenu = false
                                    navController.navigate("screen_crear_partido/${comp.idDocumento}")
                                },
                                text = { Text(text = "Crear partido") }
                            )
                        }
                    }
                }
            },
        ) {innerPadding ->
            BodyContentCompeticion(innerPadding, navController, comp)
        }
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Advertencia") },
                text = { Text("Para añadir competiciones a favoritos\nes necesario iniciar sesión") },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BodyContentCompeticion(innerPadding: PaddingValues, navController: NavController, comp: Comp) {
    val isLoadingPartido = remember { mutableStateOf(true) }
    val partidosComp = remember { mutableStateListOf<Partido>() }

    LaunchedEffect(comp.idDocumento) {
        partidosComp.addAll(getPartidosComp(comp.idDocumento))
        isLoadingPartido.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(Constantes.redondeoBoton))
                        .background(colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        comp.codigo.uppercase(Locale.ROOT),
                        color = colorScheme.onSecondaryContainer,
                        fontSize = 30.sp
                    )
                }
                Text(text = comp.nombre + " #" + comp.idDocumento)
                Text(text = comp.creador)
            }
        }
        HorizontalDivider()

        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabItems = listOf(
            TabItem("Partidos", Icons.TwoTone.SportsSoccer, Icons.Outlined.SportsSoccer),
            TabItem("Clasificación", Icons.Filled.Equalizer, Icons.Outlined.Equalizer)
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
        ) { index ->
            if (index == 0) {
                if(isLoadingPartido.value){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
                else if(partidosComp.isEmpty()){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text("Esta competición aún no tiene\n ningún partido",
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
                        DibujarPartidos(partidosComp, navController)
                    }
                }
            } else if (index == 1) {
                val lista = listOf(
                    ItemClasificacion("Equipo 1", 10, 5, 3, 1, 1, 8, 4),
                    ItemClasificacion("Equipo 2", 9, 5, 3, 0, 2, 7, 5),
                    ItemClasificacion("Equipo 3", 8, 5, 2, 2, 1, 6, 4),
                )

                Column(
                    Modifier.padding(10.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(text = "Equipo")
                        Text(text = "Puntos")
                        Text(text = "PJ")
                        Text(text = "PG")
                        Text(text = "PE")
                        Text(text = "PP")
                        Text(text = "GF")
                        Text(text = "GC")
                    }
                    TablaClasificacion(lista)
                }
            }
        }
    }
}

suspend fun getPartidosComp(idEquipo: String): SnapshotStateList<Partido> {
    val db = Firebase.firestore
    val partidos = mutableStateListOf<Partido>()

    // Realiza una consulta a la colección "partidos"
    val partidosSnapshot = db.collection("partidos")
        .whereEqualTo("idComp", idEquipo)
        .get()
        .await()

    for (partidoDocument in partidosSnapshot.documents) {
        val idComp = partidoDocument.getString("idComp") ?: "eliminado"
        val idLocal = partidoDocument.getString("idLocal") ?: "eliminado"
        val idVisitante = partidoDocument.getString("idVisitante") ?: "eliminado"
        val idCreador = partidoDocument.getString("creador") ?: "eliminado"

        // Realiza consultas a las colecciones "competiciones" y "equipos"
        val competicionDocument = db.collection("competiciones").document(idComp).get().await()
        val localDocument = db.collection("equipos").document(idLocal).get().await()
        val visitanteDocument = db.collection("equipos").document(idVisitante).get().await()
        val creadorDocument = db.collection("users").document(idCreador).get().await()

        // Crea el objeto Partido
        val partido = Partido(
            idPatido = partidoDocument.id,
            competicion = competicionDocument.getString("competicion") ?: "eliminado",
            local = localDocument.getString("equipo") ?: "eliminado",
            visitante = visitanteDocument.getString("equipo") ?: "eliminado",
            fecha = partidoDocument.getString("fecha") ?: "",
            hora = partidoDocument.getString("hora") ?: "",
            estado = partidoDocument.getString("estado") ?: "",
            golesLocal = partidoDocument.getLong("golesLocal").toString() ?: "",
            golesVisitante = partidoDocument.getLong("golesVisitante").toString() ?: "",
            creador = creadorDocument.getString("username") ?: "eliminado",
            minutos = partidoDocument.getLong("minutos").toString() ?: "",
            ganador = partidoDocument.getString("ganador") ?: ""
        )

        // Añade el objeto Partido a la lista
        partidos += partido
    }

    return partidos
}

@Composable
fun TablaClasificacion(lista: List<ItemClasificacion>) {
    LazyColumn {
        items(lista) { item ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(colorScheme.secondaryContainer),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = item.nombre, color = colorScheme.onSecondaryContainer)
                Text(text = item.puntos.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.partidosJugados.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.partidosGanados.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.partidosEmpatados.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.partidosPerdidos.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.golesFavor.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.golesContra.toString(), color = colorScheme.onSecondaryContainer)
            }
        }
    }
}

suspend fun actualizarFavComp(idComp: String, uid: String?) = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()

    if (uid != null) {
        val userDocumentRef = db.collection("users").document(uid)

        val userDocument = userDocumentRef.get().await()
        val favComp = userDocument.get("favCompeticiones") as? MutableList<String> ?: mutableListOf()

        if (idComp in favComp) {
            favComp.remove(idComp)
        } else {
            favComp.add(idComp)
        }

        userDocumentRef.update("favCompeticiones", favComp).await()
    }
}

suspend fun existeFavComp(id: String, uid: String?): Boolean = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()

    if (uid != null) {
        val userDocument = db.collection("users").document(uid).get().await()
        val favComp = userDocument.get("favCompeticiones") as? List<String> ?: listOf()
        return@withContext id in favComp
    } else {
        return@withContext false
    }
}

suspend fun limpiarEquiposBorrados(compId: String) {
    val db = FirebaseFirestore.getInstance()
    val compDocumentRef = db.collection("competiciones").document(compId)

    val compDocument = compDocumentRef.get().await()
    val equiposComp = compDocument.get("equipos") as MutableList<String>

    val equiposCollection = db.collection("equipos")
    for (equipoID in equiposComp.toList()) {
        val equiposDocument = equiposCollection.document(equipoID).get().await()
        if (!equiposDocument.exists()) {
            equiposComp.remove(equipoID)
        }
    }

    compDocumentRef.update("equipos", equiposComp).await()
}

data class ItemClasificacion(
    val nombre: String,
    val puntos: Int,
    val partidosJugados: Int,
    val partidosGanados: Int,
    val partidosEmpatados: Int,
    val partidosPerdidos: Int,
    val golesFavor: Int,
    val golesContra: Int
)


@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewCompeticion1() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenCompeticion(navController, "codigo", "creador", "competicion", "id")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkCompeticion1() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenCompeticion(navController, "codigo", "creador", "competicion", "id")
    }
}