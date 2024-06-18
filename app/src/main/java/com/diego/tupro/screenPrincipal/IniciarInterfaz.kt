package com.diego.tupro.screenPrincipal

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.Constantes
import com.diego.tupro.ui.theme.TuproTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IniciarInterfaz(navController: NavController) {
    Scaffold (
        topBar = { BarraSuperior("")},
        bottomBar = { BarraInferior(navController) }
    ){
        // ItemInicio()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String) {
    var t = titulo.trim()
    if(t == ""){
        t = "TuPro"
    }
    Column {
        TopAppBar(
            title = {
                Text(
                    text = t,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                )
            }
        )
        HorizontalDivider(thickness = 1.dp)
    }
}
@Composable
fun DibujarColumnaItems(resultBusqueda: SnapshotStateList<ItemBusqueda>, navController: NavController) {
    val comprobarComp = remember { mutableStateOf<ItemBusqueda?>(null) }
    val comprobarEquipo = remember { mutableStateOf<ItemBusqueda?>(null) }
    val context = LocalContext.current

    LaunchedEffect(comprobarComp.value) {
        if (comprobarComp.value != null) {
            if (existeCompeticion(comprobarComp.value?.idDocumento, context)) {
                navController.navigate("screen_competicion/${comprobarComp.value?.codigo}/${comprobarComp.value?.creador}/${comprobarComp.value?.nombre}/${comprobarComp.value?.idDocumento}")
            } else {
                Toast.makeText(context, "competición no encontrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(comprobarEquipo.value) {
        if (comprobarEquipo.value != null) {
            if (existeEquipo(comprobarEquipo.value?.idDocumento, context)) {
                navController.navigate("screen_equipo/${comprobarEquipo.value?.codigo}/${comprobarEquipo.value?.creador}/${comprobarEquipo.value?.nombre}/${comprobarEquipo.value?.idDocumento}")
            } else {
                Toast.makeText(context, "equipo no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(resultBusqueda) {
            val esUser = it.tipo == "Usuario"
            ListItem(
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.2f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(Constantes.redondeoBoton))
                            .background(colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            it.codigo.uppercase(Locale.ROOT),
                            color = colorScheme.onSecondaryContainer,
                            fontSize = 20.sp
                        )
                    }
                },
                headlineContent = { Text(it.nombre) },
                supportingContent = { if(!esUser) Text("#" + it.idDocumento) },
                trailingContent = { if(!esUser) Text(it.tipo + ": " + it.creador) else Text(it.tipo)},
                modifier = Modifier
                    .clickable {
                        if (it.tipo == "Equipo") comprobarEquipo.value = it
                        else if (it.tipo == "Competicion") comprobarComp.value = it
                        else if (it.tipo == "Usuario") {
                            val auth = FirebaseAuth.getInstance()
                            val uid = auth.currentUser?.uid
                            if (uid != it.idDocumento) navController.navigate("screen_perfil/${it.nombre}/${it.idDocumento}")
                            else navController.navigate("item_perfil")
                        }
                    }
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun BarraInferior(navController: NavController) {
    val items = listOf(
        BottomNavigationItem("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavigationItem("Buscar", Icons.Default.SavedSearch, Icons.Outlined.Search),
        BottomNavigationItem("Favoritos", Icons.Default.Favorite, Icons.Default.FavoriteBorder),
        BottomNavigationItem("Perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            "item_inicio" -> selectedItemIndex = 0
            "item_buscar" -> selectedItemIndex = 1
            "item_favoritos" -> selectedItemIndex = 2
            "item_perfil" -> selectedItemIndex = 3
        }
    }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = if (index == selectedItemIndex){item.selecIcon} else item.unselecIcon, contentDescription = item.titulo)
                },
                label = { Text(text = item.titulo) },
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navController.navigate("item_" + item.titulo.lowercase())
                }
            )
        }
    }
}

@Composable
fun DibujarPartidos(listaPartidos: SnapshotStateList<Partido>, navController: NavController) {
    val idPartido = remember { mutableStateOf<String?>(null) }
    val creador = remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(idPartido.value) {
        val idL = getIdEquipo(idPartido.value, true)
        val idV = getIdEquipo(idPartido.value, false)
        if (idPartido.value != null) {
            if (existePartido(idPartido.value, context) && existeEquipo(idL, context) && existeEquipo(idV, context)) {
                navController.navigate("screen_partido/${idPartido.value!!}/${creador.value}")
            } else {
                Toast.makeText(context, "partido no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(listaPartidos) { partido ->
            // card
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                shape = RoundedCornerShape(Constantes.redondeoBoton),
                onClick = {
                    creador.value = partido.creador
                    idPartido.value = partido.idPartido
                }

            ) {
                // competicion
                Row(
                    Modifier
                        .background(colorScheme.surface)
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 10.dp, bottom = 4.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(partido.competicion)
                    when (partido.estado) {
                        "nuevo" -> Text(partido.fecha)
                        "finalizado" -> {}
                        else -> Text(partido.minutos + "'")
                    }
                    if (partido.estado != "nuevo" && partido.estado != "enJuego") Text(partido.estado)
                }

                HorizontalDivider(
                    color = colorScheme.outline,
                    thickness = 0.dp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )

                Row(
                    Modifier
                        .background(colorScheme.surface)
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 20.dp, start = 25.dp, end = 25.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // local
                    Box(
                        Modifier
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            partido.local,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = colorScheme.onSurface
                        )
                    }
                    // hora
                    Box(
                        Modifier
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (partido.estado != "nuevo") partido.golesLocal + " - " + partido.golesVisitante else partido.hora,
                            textAlign = TextAlign.Center,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            style = TextStyle(textAlign = TextAlign.Center)
                        )
                    }
                    // visitante
                    Box(
                        Modifier
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            partido.visitante,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

suspend fun getIdEquipo(idPartido: String?, esLocal: Boolean): String? {
    val db = Firebase.firestore
    var campo = "idVisitante"
    if (esLocal) campo = "idLocal"

    // referencia al documento del partido
    val partidoRef = idPartido?.let { db.collection("partidos").document(it) }

    // partido de la base de datos
    val partido = partidoRef?.get()?.await()

    // Comprueba si el partido existe
    return if (partido?.exists() == true) {
        partido.getString(campo)
    } else {
        // El partido no existe
        null
    }
}

suspend fun existePartido(idPartido: String?, context: Context): Boolean {
    if (idPartido == null) return false

    // Comprobar la conectividad a Internet
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    val isConnected = activeNetwork?.isConnectedOrConnecting == true

    if (!isConnected) {
        // No hay conexión a Internet
        return false
    }

    val db = FirebaseFirestore.getInstance()
    val partidoDocument = db.collection("partidos").document(idPartido).get().await()
    return partidoDocument.exists()
}

suspend fun existeCompeticion(idComp: String?, context: Context): Boolean {
    if (idComp == null) return false

    // Comprobar la conectividad a Internet
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    val isConnected = activeNetwork?.isConnectedOrConnecting == true

    if (!isConnected) {
        // No hay conexión a Internet
        return false
    }

    val db = FirebaseFirestore.getInstance()
    val competicionDocument = db.collection("competiciones").document(idComp).get().await()
    return competicionDocument.exists()
}

suspend fun existeEquipo(idEquipo: String?, context: Context): Boolean {
    if (idEquipo == null) return false

    // Comprobar la conectividad a Internet
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    val isConnected = activeNetwork?.isConnectedOrConnecting == true

    if (!isConnected) {
        // No hay conexión a Internet
        return false
    }

    val db = FirebaseFirestore.getInstance()
    val equipoDocument = db.collection("equipos").document(idEquipo).get().await()
    return equipoDocument.exists()
}


data class BottomNavigationItem(
    val titulo: String,
    val selecIcon: ImageVector,
    val unselecIcon: ImageVector
)

@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        IniciarInterfaz(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDark() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        IniciarInterfaz(navController)
    }
}