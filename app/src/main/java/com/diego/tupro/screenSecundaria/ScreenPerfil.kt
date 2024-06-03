package com.diego.tupro.screenSecundaria

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.diego.tupro.screenPrincipal.DibujarColumnaItems
import com.diego.tupro.screenPrincipal.ItemBusqueda
import com.diego.tupro.screenPrincipal.TabItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenPerfil(navController: NavController, nombreUsuario: String, id: String) {
    val aportaciones = remember { mutableIntStateOf(0) }
    LaunchedEffect(id) {
        aportaciones.intValue = getNumeroDeRegistros(id)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = nombreUsuario,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
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
                        Text(
                            text = nombreUsuario.substring(0, 1),
                            color = colorScheme.onSecondaryContainer,
                            fontSize = 26.sp
                        )
                    }
                    Text(text = "Aportaciones: ${aportaciones.value}", fontSize = 22.sp, modifier = Modifier.padding(start = 20.dp))
                }
                HorizontalDivider()
            }
        }
    ) {
        BodyContentScreenPerfil(it, navController, id)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BodyContentScreenPerfil(paddingValues: PaddingValues, navController: NavController, uid: String) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabItems = listOf(
        TabItem("Equipos", Icons.Filled.Shield, Icons.Outlined.Shield),
        TabItem("Competiciones", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
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
            .padding(paddingValues),
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
                val isLoading = remember { mutableStateOf(true) }
                val equipos = remember { mutableStateListOf<ItemBusqueda>() }
                LaunchedEffect(uid) {
                    equipos.addAll(getEquiposDelUsuario(uid))
                    isLoading.value = false
                }
                if(isLoading.value){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
                else if(equipos.isEmpty()){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text("Este usuario aún no ha creado\nningún equipo",
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
                        DibujarColumnaItems(equipos, navController)
                    }
                }
            } else if (index == 1) {
                val isLoading = remember { mutableStateOf(true) }
                val competiciones = remember { mutableStateListOf<ItemBusqueda>() }
                LaunchedEffect(uid) {
                    competiciones.addAll(getCompeticionesDelUsuario(uid))
                    isLoading.value = false
                }
                if(isLoading.value){
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
                        Text("Este usuario aún no ha creado\nninguna competición",
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

suspend fun getCompeticionesDelUsuario(uid: String): SnapshotStateList<ItemBusqueda> {
    val db = FirebaseFirestore.getInstance()
    val competiciones = mutableStateListOf<ItemBusqueda>()

    val querySnapshot = db.collection("competiciones")
        .whereEqualTo("creador", uid)
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

suspend fun getEquiposDelUsuario(uid: String): SnapshotStateList<ItemBusqueda> {
    val db = FirebaseFirestore.getInstance()
    val equipos = mutableStateListOf<ItemBusqueda>()

    val querySnapshot = db.collection("equipos")
        .whereEqualTo("creador", uid)
        .get()
        .await()

    for (document in querySnapshot.documents) {
        val codigo = document.getString("codigo") ?: ""
        val nombre = document.getString("equipo") ?: ""
        val idDocumento = document.id
        var creador = document.getString("creador") ?: ""

        val userDocument = db.collection("users").document(creador).get().await()
        creador = userDocument.getString("username") ?: ""

        val itemBusqueda = ItemBusqueda(codigo, nombre, idDocumento, creador, "Equipo")
        equipos.add(itemBusqueda)
    }

    return equipos
}

suspend fun getNumeroDeRegistros(id: String): Int {
    val db = FirebaseFirestore.getInstance()

    val querySnapshotCompeticiones = db.collection("competiciones")
        .whereEqualTo("creador", id)
        .get()
        .await()

    val numeroDeCompeticiones = querySnapshotCompeticiones.size()

    val querySnapshotEquipos = db.collection("equipos")
        .whereEqualTo("creador", id)
        .get()
        .await()

    val numeroDeEquipos = querySnapshotEquipos.size()

    return numeroDeCompeticiones + numeroDeEquipos
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewScreenPerfil() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenPerfil(navController, "nombre", "id")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkScreenPerfil() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenPerfil(navController, "nombre", "id")
    }
}