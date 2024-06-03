package com.diego.tupro.screenSecundaria

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.SpeakerNotes
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.SpeakerNotes
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.Constantes
import com.diego.tupro.SessionManager
import com.diego.tupro.navigation.AppScreens
import com.diego.tupro.screenPrincipal.Comp
import com.diego.tupro.screenPrincipal.Equipo
import com.diego.tupro.screenPrincipal.Partido
import com.diego.tupro.screenPrincipal.TabItem
import com.diego.tupro.ui.theme.TuproTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenPartido(navController: NavController, idPartido: String, creadorNombre: String) {
    val sessionManager = SessionManager(LocalContext.current)
    val user = sessionManager.getUserDetails()

    var equipoLocal by remember { mutableStateOf(Equipo("LBFS", "la bañeza", "1", "")) }
    var equipoVisitante by remember { mutableStateOf(Equipo("LBFS", "la bañeza", "1", "")) }
    var comp by remember { mutableStateOf(Comp("LBFS", "la bañeza", "1", "")) }
    var partido by remember { mutableStateOf(Partido("LBFS", "la bañeza", "1", "", "", "", "", "", "", "", "", "")) }


    var expanded by remember { mutableStateOf(false) }
    var expandedEdit by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    var isLoadingPartido by remember { mutableStateOf(true) }

    var actualizarEstado by remember { mutableStateOf(false) }
    var nuevoEstado by remember { mutableStateOf("") }

    LaunchedEffect(isLoadingPartido) {
        if (isLoadingPartido){
            partido = getPartidoPorId(idPartido)
            comp = getCompeticionPorId(idPartido)
            equipoLocal = getEquipoPorId(idPartido, true)
            equipoVisitante = getEquipoPorId(idPartido, false)
            isLoadingPartido = false
        }
    }

    LaunchedEffect(actualizarEstado) {
        if (actualizarEstado) {
            funIniciarPartido(partido.idPartido, nuevoEstado)
            isLoadingPartido = true
            actualizarEstado = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { if (!isLoadingPartido) Text(equipoLocal.codigo + " - " + equipoVisitante.codigo) else Text("")},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "atrás")
                        }
                    },
                    actions = {
                        if (!isLoadingPartido && user["username"] == creadorNombre) {
                            if (partido.estado != "nuevo") {
                                IconButton(onClick = { expandedEdit = true }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "editar partido")
                                }
                                DropdownMenu(
                                    expanded = expandedEdit,
                                    onDismissRequest = { expandedEdit = false }
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            expandedEdit = false
                                        },
                                        text = { Text(text = "Añadir evento") }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expandedEdit = false
                                        },
                                        text = { Text(text = "Añadir narración") }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expandedEdit = false
                                        },
                                        text = { Text(text = "Editar marcador") }
                                    )
                                }
                            }

                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Filled.Settings, contentDescription = "ajustes del partido")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                if (partido.estado != "finalizado") {
                                    if (partido.estado == "nuevo") {
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "enJuego"
                                                actualizarEstado = true
                                            },
                                            text = { Text(text = "Iniciar partido") }
                                        )
                                    }
                                    if (partido.estado == "enJuego"){
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "detenido"
                                                actualizarEstado = true
                                            },
                                            text = { Text(text = "Detener partido") }
                                        )
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "descanso"
                                                actualizarEstado = true
                                            },
                                            text = { Text(text = "Descanso") }
                                        )
                                    }
                                    if (partido.estado == "descanso" || partido.estado == "detenido"){
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "enJuego"
                                                actualizarEstado = true
                                            },
                                            text = { Text(text = "Reanudar") }
                                        )
                                    }
                                    if (partido.estado != "nuevo"){
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "finalizado"
                                                actualizarEstado = true
                                            },
                                            text = { Text(text = "Finalizar") }
                                        )
                                    }
                                }

                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        openDialog = true
                                    },
                                    text = { Text(text = "Eliminar partido") },
                                    modifier = Modifier.background(colorScheme.errorContainer),
                                    colors = MenuDefaults.itemColors(
                                        colorScheme.onErrorContainer
                                    )
                                )
                            }
                            if (openDialog) {
                                AlertDialog(
                                    onDismissRequest = { openDialog = false },
                                    title = { Text("Confirmar eliminación") },
                                    text = { Text("¿Estás seguro de que quieres eliminar el partido?") },
                                    confirmButton = {
                                        FilledTonalButton(
                                            onClick = {
                                                openDialog = false
                                                // TODO: Eliminar partido
                                            },
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                colorScheme.errorContainer
                                            )
                                        ) {
                                            Text("Confirmar")
                                        }
                                    },
                                    dismissButton = {
                                        Button(onClick = { openDialog = false }) {
                                            Text("Cancelar")
                                        }
                                    }
                                )
                            }
                        }
                    }
                )
                HorizontalDivider(thickness = 1.dp)
            }
        }
    ) {
        BodyContentPartido(it, equipoLocal, equipoVisitante, comp, partido, isLoadingPartido, navController)
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BodyContentPartido(
    paddingValues: PaddingValues,
    equipoLocal: Equipo,
    equipoVisitante: Equipo,
    comp: Comp,
    partido: Partido,
    isLoadingPartido: Boolean,
    navController: NavController
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var fila1 = partido.hora
    var fila2 = partido.fecha

    if (partido.estado != "nuevo") {
        fila1 = partido.golesLocal + " - " + partido.golesVisitante
        fila2 = partido.minutos + "'"
        if (partido.estado != "enJuego") {
            fila2 = partido.estado
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        if (isLoadingPartido) {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else{
            // fila competicion
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("${AppScreens.ScreenCompeticion.route}/${comp.codigo}/${comp.creador}/${comp.nombre}/${comp.idDocumento}")
                    }
            ) {
                Text(
                    text = comp.nombre,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            // fila marcador
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp, top = 12.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate("${AppScreens.ScreenEquipo.route}/${equipoLocal.codigo}/${equipoLocal.creador}/${equipoLocal.equipo}/${equipoLocal.idDocumento}")
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(Constantes.redondeoBoton))
                            .background(colorScheme.secondaryContainer)
                            .border(
                                width = 1.dp,
                                color = colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(Constantes.redondeoBoton)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = equipoLocal.codigo.uppercase(),
                            color = colorScheme.onSecondaryContainer,
                            fontSize = 24.sp
                        )
                    }
                    Text(text = equipoLocal.equipo + " #" + equipoLocal.idDocumento)
                    Text(text = equipoLocal.creador)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = fila1,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text(text = fila2, fontSize = 18.sp)
                }
                Column(
                    modifier = Modifier.weight(1f)
                        .clickable {
                            navController.navigate("${AppScreens.ScreenEquipo.route}/${equipoVisitante.codigo}/${equipoVisitante.creador}/${equipoVisitante.equipo}/${equipoVisitante.idDocumento}")
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(Constantes.redondeoBoton))
                            .background(colorScheme.secondaryContainer)
                            .border(
                                width = 1.dp,
                                color = colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(Constantes.redondeoBoton)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = equipoVisitante.codigo.uppercase(),
                            color = colorScheme.onSecondaryContainer,
                            fontSize = 24.sp
                        )
                    }
                    Text(text = equipoVisitante.equipo + " #" + equipoVisitante.idDocumento)
                    Text(text = equipoVisitante.creador)
                }
            }
            HorizontalDivider(thickness = 1.dp)

            val tabItems = listOf(
                TabItem(
                    "Eventos",
                    Icons.AutoMirrored.Filled.EventNote,
                    Icons.AutoMirrored.Outlined.EventNote
                ),
                TabItem(
                    "Narración",
                    Icons.AutoMirrored.Filled.SpeakerNotes,
                    Icons.AutoMirrored.Outlined.SpeakerNotes
                )
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
                    .fillMaxSize()
            ) { index ->
                if (index == 0) {
                    val eventosDePrueba = listOf(
                        Evento("1", "Gol", "jugador", "15'", "local", "gol"),
                        Evento(
                            "2",
                            "Tarjeta amarilla",
                            "jugador",
                            "30'",
                            "visitante",
                            "tarjeta"
                        ),
                        Evento("3", "Gol", "12345678901234567890", "45'", "visitante", "gol"),
                        Evento(
                            "4",
                            "Tarjeta roja",
                            "12345678901234567890",
                            "60'",
                            "local",
                            "cambio"
                        )
                    )

                    MostrarEventos(eventosDePrueba)
                } else if (index == 1) {
                    val comentariosDePrueba = listOf(
                        Comentario(
                            "1",
                            "Gol del equipo local",
                            "El equipo local ha marcado un gol espectacular",
                            "15'"
                        ),
                        Comentario(
                            "2",
                            "Tarjeta amarilla",
                            "El jugador del equipo visitante ha recibido una tarjeta amarilla",
                            "30'"
                        ),
                        Comentario(
                            "3",
                            "Gol del equipo visitante",
                            "El equipo visitante ha empatado el partido con un golazo",
                            "45'"
                        ),
                        Comentario(
                            "4",
                            "Tarjeta roja",
                            "Desafortunadamente, un jugador del equipo local ha sido expulsado",
                            "60'"
                        )
                    )
                    MostrarComentarios(comentariosDePrueba)
                }
            }
        }
    }
}

@Composable
fun MostrarEventos(eventos: List<Evento>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(eventos) { evento ->
            val local = evento.tipoEquipo == "local"
            val iconos = mapOf(
                "gol" to Icons.Default.SportsSoccer,
                "tarjeta" to Icons.Default.Sports,
                "cambio" to Icons.AutoMirrored.Filled.CompareArrows
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column(
                        modifier = Modifier.weight(0.9f)
                    ){
                        if (local) {
                            Text(text = evento.titulo, fontWeight = FontWeight.Bold)
                            Text(text = evento.texto)
                        }
                    }
                    if (local) {
                        iconos[evento.tipoEvento]?.let {
                            Icon(
                                it,
                                contentDescription = evento.tipoEvento,
                                modifier = Modifier
                                    .padding(start = 12.dp),
                                tint = colorScheme.primary
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(0.5f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = evento.minuto,
                        fontSize = 22.sp,
                        color = colorScheme.primary,
                    )
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if (!local) {
                        iconos[evento.tipoEvento]?.let { Icon(
                            it,
                            contentDescription = evento.tipoEvento,
                            modifier = Modifier
                                .padding(end = 12.dp),
                            tint = colorScheme.primary
                        ) }
                    }
                    Column(
                        modifier = Modifier.weight(0.9f)
                    ){
                        if (!local) {
                            Text(text = evento.titulo, fontWeight = FontWeight.Bold)
                            Text(text = evento.texto)
                        }
                    }
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun MostrarComentarios(comentarios: List<Comentario>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        items(comentarios) { comentario ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(0.2f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = comentario.minuto,
                        fontSize = 22.sp,
                        color = colorScheme.primary,
                    )
                }
                Column(
                    modifier = Modifier.weight(0.8f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = comentario.titulo, fontWeight = FontWeight.Bold)
                    Text(text = comentario.texto)
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
        }
    }
}

suspend fun getPartidoPorId(idPartido: String): Partido {
    val db = FirebaseFirestore.getInstance()
    val partidoDocument = db.collection("partidos").document(idPartido).get().await()
    return Partido(
        idPartido = partidoDocument.id,
        competicion = partidoDocument.getString("idComp") ?: "eliminado",
        local = partidoDocument.getString("idLocal") ?: "eliminado",
        visitante = partidoDocument.getString("idVisitante") ?: "eliminado",
        fecha = partidoDocument.getString("fecha") ?: "",
        hora = partidoDocument.getString("hora") ?: "",
        estado = partidoDocument.getString("estado") ?: "",
        golesLocal = partidoDocument.getLong("golesLocal").toString(),
        golesVisitante = partidoDocument.getLong("golesVisitante").toString(),
        creador = partidoDocument.getString("creador") ?: "eliminado",
        minutos = partidoDocument.getLong("minutos").toString(),
        ganador = partidoDocument.getString("ganador") ?: ""
    )
}

suspend fun getEquipoPorId(idPartido: String, isLocal: Boolean): Equipo {
    var consulta = "idLocal"
    if (!isLocal) {
        consulta = "idVisitante"
    }

    val db = FirebaseFirestore.getInstance()
    val partidoDocument = db.collection("partidos").document(idPartido).get().await()
    val idEquipo = partidoDocument.getString(consulta) ?: ""

    val equipoDocument = db.collection("equipos").document(idEquipo).get().await()
    val idCreador = equipoDocument.getString("creador") ?: ""

    val creadorDocument = db.collection("users").document(idCreador).get().await()
    return Equipo(
        codigo = equipoDocument.getString("codigo")?.uppercase() ?: "",
        equipo = equipoDocument.getString("equipo") ?: "",
        idDocumento = equipoDocument.id,
        creador = creadorDocument.getString("username") ?: ""
    )
}

suspend fun getCompeticionPorId(idPartido: String): Comp {
    val db = FirebaseFirestore.getInstance()
    val partidoDocument = db.collection("partidos").document(idPartido).get().await()
    val idCompeticion = partidoDocument.getString("idComp") ?: ""
    val idCreador = partidoDocument.getString("creador") ?: ""

    val competicionDocument = db.collection("competiciones").document(idCompeticion).get().await()
    val creadorDocument = db.collection("users").document(idCreador).get().await()
    return Comp(
        codigo = competicionDocument.getString("codigo") ?: "",
        nombre = competicionDocument.getString("competicion") ?: "",
        idDocumento = competicionDocument.id,
        creador = creadorDocument.getString("username") ?: ""
    )
}

suspend fun funIniciarPartido(idPartido: String, nuevoEstado: String) {
    val db = FirebaseFirestore.getInstance()
    val myRef = db.collection("partidos").document(idPartido)

    myRef.update("estado", nuevoEstado).await()
}

data class Evento(
    val idEvento: String,
    val titulo: String,
    val texto: String,
    val minuto: String,
    val tipoEquipo: String,
    val tipoEvento: String
)

data class Comentario(
    val idComentario: String,
    val titulo: String,
    val texto: String,
    val minuto: String
)

@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenPartido(navController, "", "")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDark() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenPartido(navController, "", "")
    }
}