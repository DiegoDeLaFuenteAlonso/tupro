package com.diego.tupro.screenPrincipal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.diego.tupro.Constantes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun ItemFavoritos(navController: NavController) {
    Scaffold(
        topBar = { BarraSuperior(titulo = "Favoritos")},
        bottomBar = { BarraInferior(navController = navController)}
    ) {
        BodyContentFavoritos(it, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BodyContentFavoritos(innerPadding: PaddingValues, navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(Constantes.favSelectedTabIndex) }
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    val equiposFav = remember { mutableStateListOf<ItemBusqueda>() }
    val compFav = remember { mutableStateListOf<ItemBusqueda>() }
    val isLoadingEquipos = remember { mutableStateOf(true) }
    val isLoadingComp = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (uid != null) {
            limpiarEquiposFavoritas(uid)
            equiposFav.addAll(getEquiposFavoritos(uid))
        }
        isLoadingEquipos.value = false
    }
    LaunchedEffect(Unit) {
        if (uid != null) {
            limpiarCompeticionesFavoritas(uid)
            compFav.addAll(getCompeticionesFavoritos(uid))
        }
        isLoadingComp.value = false
    }

    val tabItems = listOf(
        TabItem("Equipos", Icons.Filled.Shield, Icons.Outlined.Shield),
        TabItem("Competiciones", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
    )
    val pagerState = rememberPagerState {
        tabItems.size
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
        Constantes.favSelectedTabIndex = selectedTabIndex
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
            if (index == 0) {
                if(uid == null){
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Accede con tu cuenta para \nañadir equipos favoritos",
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else if(isLoadingEquipos.value){
                    Box (
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ){
                        CircularProgressIndicator()
                    }
                }
                else if(equiposFav.isEmpty()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Aún no tienes equipos\nfavoritos añdidos",
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else{
                    DibujarColumnaItems(equiposFav, navController)
                }
            } else if (index == 1) {
                if(uid == null){
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Accede con tu cuenta para \nañadir competiciones favoritas",
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if(isLoadingComp.value){
                    Box (
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ){
                        CircularProgressIndicator()
                    }
                }
                else if(equiposFav.isEmpty()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Aún no tienes competiciones\n" +
                                    "favoritas añdidas",
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else{
                    DibujarColumnaItems(compFav, navController)
                }
            }
        }
    }
}

suspend fun getEquiposFavoritos(userId: String): SnapshotStateList<ItemBusqueda> {
    val db = FirebaseFirestore.getInstance()
    val userDocument = db.collection("users").document(userId).get().await()
    val favEquipos = userDocument.get("favEquipos") as List<String>

    val equiposFavoritos = SnapshotStateList<ItemBusqueda>()
    for (equipoId in favEquipos) {
        val equipoDocument = db.collection("equipos").document(equipoId).get().await()

        val codigoE = equipoDocument.getString("codigo") ?: ""
        val nombre = equipoDocument.getString("equipo") ?: ""
        val idDocumento = equipoDocument.id
        val creadorId = equipoDocument.getString("creador") ?: ""

        val creadorDocument = db.collection("users").document(creadorId).get().await()
        val creadorNombre = creadorDocument.getString("username") ?: ""

        val equipo = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Equipo")
        equiposFavoritos.add(equipo)
    }
    return equiposFavoritos
}

suspend fun getCompeticionesFavoritos(userId: String): SnapshotStateList<ItemBusqueda> {
    val db = FirebaseFirestore.getInstance()
    val userDocument = db.collection("users").document(userId).get().await()
    val favComp = userDocument.get("favCompeticiones") as List<String>

    val compFavoritos = SnapshotStateList<ItemBusqueda>()
    for (compId in favComp) {
        val equipoDocument = db.collection("competiciones").document(compId).get().await()

        val codigoC = equipoDocument.getString("codigo") ?: ""
        val nombre = equipoDocument.getString("competicion") ?: ""
        val idDocumento = equipoDocument.id
        val creadorId = equipoDocument.getString("creador") ?: ""

        val creadorDocument = db.collection("users").document(creadorId).get().await()
        val creadorNombre = creadorDocument.getString("username") ?: ""

        val competicion = ItemBusqueda(codigoC, nombre, idDocumento, creadorNombre, "Competicion")
        compFavoritos.add(competicion)
    }
    return compFavoritos
}

suspend fun limpiarEquiposFavoritas(userId: String) {
    val db = FirebaseFirestore.getInstance()
    val userDocumentRef = db.collection("users").document(userId)

    val userDocument = userDocumentRef.get().await()
    val favEquipos = userDocument.get("favEquipos") as MutableList<String>

    val equiposCollection = db.collection("equipos")
    for (equipoId in favEquipos.toList()) {
        val equipoDocument = equiposCollection.document(equipoId).get().await()
        if (!equipoDocument.exists()) {
            favEquipos.remove(equipoId)
        }
    }

    // Actualizamos el documento del usuario con el array favCompeticiones modificado
    userDocumentRef.update("favEquipos", favEquipos).await()
}

suspend fun limpiarCompeticionesFavoritas(userId: String) {
    val db = FirebaseFirestore.getInstance()
    val userDocumentRef = db.collection("users").document(userId)

    val userDocument = userDocumentRef.get().await()
    val favCompeticiones = userDocument.get("favCompeticiones") as MutableList<String>

    val competicionesCollection = db.collection("competiciones")
    for (competicionId in favCompeticiones.toList()) {  // Creamos una copia para evitar errores de concurrencia
        val competicionDocument = competicionesCollection.document(competicionId).get().await()
        if (!competicionDocument.exists()) {
            // Si la competición no existe, la eliminamos del array favCompeticiones
            favCompeticiones.remove(competicionId)
        }
    }

    // Actualizamos el documento del usuario con el array favCompeticiones modificado
    userDocumentRef.update("favCompeticiones", favCompeticiones).await()
}