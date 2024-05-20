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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.twotone.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.diego.tupro.screenPrincipal.DibujarColumnaItems
import com.diego.tupro.screenPrincipal.DibujarPartidos
import com.diego.tupro.screenPrincipal.Equipo
import com.diego.tupro.screenPrincipal.ItemBusqueda
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
fun ScreenEquipo(
    navController: NavController,
    codigo: String,
    creador: String,
    nombre: String,
    id: String
) {
    val equipo = Equipo(codigo, nombre, id, creador)
    val esFav = remember { mutableStateOf(false) }
    val actualizarFav = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    LaunchedEffect(Unit) {
        esFav.value = existeFavEquipos(equipo.idDocumento, uid)
    }
    LaunchedEffect(actualizarFav.value) {
        if (actualizarFav.value) {
            actualizarFavEquipos(equipo.idDocumento, uid)
            actualizarFav.value = false
            esFav.value = !esFav.value
        }
    }
    Scaffold(
        topBar = {
            Column{
                TopAppBar(
                    title = {
                        Text(text = "Equipo", color = colorScheme.primary, fontSize = 26.sp)
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
        }
    ) {innerPadding ->
        BodyContentEquipo(innerPadding, navController, equipo)
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Advertencia") },
            text = { Text("Para añadir equipos a favoritos\nes necesario iniciar sesión") },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BodyContentEquipo(innerPadding: PaddingValues, navController: NavController, equipo: Equipo) {
    val competiciones = remember { mutableStateListOf<ItemBusqueda>() }
    val isLoadingComp = remember { mutableStateOf(true) }
    val isLoadingPartido = remember { mutableStateOf(true) }
    val partidosEquipo = remember { mutableStateListOf<Partido>() }

    LaunchedEffect(equipo.idDocumento) {
        competiciones.addAll(getCompeticiones(equipo.idDocumento))
        isLoadingComp.value = false
    }

    LaunchedEffect(equipo.idDocumento) {
        partidosEquipo.addAll(getPartidosLocal(equipo.idDocumento))
        partidosEquipo.addAll(getPartidosVisitante(equipo.idDocumento))
        isLoadingPartido.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ){
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
                        equipo.codigo.uppercase(Locale.ROOT),
                        color = colorScheme.onSecondaryContainer,
                        fontSize = 30.sp
                    )
                }
                Text(text = equipo.equipo + " #" + equipo.idDocumento)
                Text(text = equipo.creador)
            }
        }
        HorizontalDivider()
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabItems = listOf(
            TabItem("Partidos", Icons.TwoTone.SportsSoccer, Icons.Outlined.SportsSoccer),
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
                else if(partidosEquipo.isEmpty()){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text("Este equipo aún no está\nen ningún partido",
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
                        DibujarPartidos(partidosEquipo, navController)
                    }
                }
            } else if (index == 1) {
                if(isLoadingComp.value){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
                else if(competiciones.isEmpty()){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text("Este equipo aún no está\nen ninguna competición",
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
                        DibujarColumnaItems(competiciones, navController)
                    }
                }
            }
        }
    }
}

suspend fun getCompeticiones(id: String): SnapshotStateList<ItemBusqueda> {
    val db = FirebaseFirestore.getInstance()
    val competiciones = mutableStateListOf<ItemBusqueda>()

    val querySnapshot = db.collection("competiciones")
        .whereArrayContains("equipos", id)
        .get()
        .await()

    for (document in querySnapshot.documents) {
        val codigo = document.getString("codigo") ?: ""
        val nombre = document.getString("competicion") ?: ""
        val idDocumento = document.id
        var creador = document.getString("creador") ?: ""

        val userDocument = db.collection("users").document(creador).get().await()
        creador = userDocument.getString("username") ?: ""

        val itemBusqueda = ItemBusqueda(codigo, nombre, idDocumento, creador, "Competicion")
        competiciones.add(itemBusqueda)
    }

    return competiciones
}

suspend fun actualizarFavEquipos(idEquipo: String, uid: String?) = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()

    if (uid != null) {
        val userDocumentRef = db.collection("users").document(uid)

        val userDocument = userDocumentRef.get().await()
        val favEquipos = userDocument.get("favEquipos") as? MutableList<String> ?: mutableListOf()

        if (idEquipo in favEquipos) {
            favEquipos.remove(idEquipo)
        } else {
            favEquipos.add(idEquipo)
        }

        userDocumentRef.update("favEquipos", favEquipos).await()
    }
}

suspend fun existeFavEquipos(id: String, uid: String?): Boolean = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()

    if (uid != null) {
        val userDocument = db.collection("users").document(uid).get().await()
        val favEquipos = userDocument.get("favEquipos") as? List<String> ?: listOf()
        return@withContext id in favEquipos
    } else {
        return@withContext false
    }
}

suspend fun getPartidosLocal(idEquipo: String): SnapshotStateList<Partido> {
    val db = Firebase.firestore
    val partidos = mutableStateListOf<Partido>()

    // Realiza una consulta a la colección "partidos"
    val partidosSnapshot = db.collection("partidos")
        .whereEqualTo("idLocal", idEquipo)
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

suspend fun getPartidosVisitante(idEquipo: String): SnapshotStateList<Partido> {
    val db = Firebase.firestore
    val partidos = mutableStateListOf<Partido>()

    // Realiza una consulta a la colección "partidos"
    val partidosSnapshot = db.collection("partidos")
        .whereEqualTo("idVisitante", idEquipo)
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


@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewEquipo1() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenEquipo(navController, "LB", "Diego", "La Bañeza", "1")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkEquipo1() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenEquipo(navController, "", "", "", "")
    }
}