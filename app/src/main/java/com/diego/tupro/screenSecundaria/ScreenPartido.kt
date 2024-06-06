package com.diego.tupro.screenSecundaria

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ActualizarMinutosService
import com.diego.tupro.Constantes
import com.diego.tupro.SessionManager
import com.diego.tupro.navigation.AppScreens
import com.diego.tupro.screenPrincipal.Comp
import com.diego.tupro.screenPrincipal.Equipo
import com.diego.tupro.screenPrincipal.Partido
import com.diego.tupro.screenPrincipal.TabItem
import com.diego.tupro.screenPrincipal.existeCompeticion
import com.diego.tupro.screenPrincipal.existeEquipo
import com.diego.tupro.ui.theme.TuproTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenPartido(navController: NavController, idPartido: String, creadorNombre: String) {
    val sessionManager = SessionManager(LocalContext.current)
    val user = sessionManager.getUserDetails()

    var equipoLocal by remember { mutableStateOf(Equipo("", "", "", "")) }
    var equipoVisitante by remember { mutableStateOf(Equipo("", "", "", "")) }
    var comp by remember { mutableStateOf(Comp("", "", "", "")) }
    val partido = remember { mutableStateOf(Partido("", "", "", "", "", "", "", "", "", "", "", "")) }


    var expanded by remember { mutableStateOf(false) }
    var expandedEdit by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    val isLoadingPartido = remember { mutableStateOf(true) }

    var actualizarEstado by remember { mutableStateOf(false) }
    var nuevoEstado by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        escucharCambiosPartido(idPartido, partido, isLoadingPartido)
    }

    LaunchedEffect(partido.value) {
        if (partido.value.idPartido != "") {
            isLoadingPartido.value = true
            //verificarEquiposCompeticion(idPartido)
            comp = getCompeticionPorId(idPartido)
            equipoLocal = getEquipoPorId(idPartido, true)
            equipoVisitante = getEquipoPorId(idPartido, false)
            isLoadingPartido.value = false
        }
    }

    LaunchedEffect(actualizarEstado) {
        if (actualizarEstado) {
            funActualizarEstadoPartido(partido.value.idPartido, nuevoEstado, resultado)
            //isLoadingPartido = true
            actualizarEstado = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { if (!isLoadingPartido.value) Text(equipoLocal.codigo + " - " + equipoVisitante.codigo) else Text("")},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "atrás")
                        }
                    },
                    actions = {
                        if (!isLoadingPartido.value && user["username"] == creadorNombre) {
                            if (partido.value.estado != "nuevo") {
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
                                            navController.navigate(  "${AppScreens.ScreenEvento.route}/${equipoLocal.codigo}/${equipoVisitante.codigo}/${partido.value.idPartido}/${partido.value.minutos}")
                                        },
                                        text = { Text(text = "Añadir evento") }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expandedEdit = false
                                            navController.navigate( "${AppScreens.ScreenNarracion.route}/${equipoLocal.codigo}/${equipoVisitante.codigo}/${partido.value.idPartido}/${partido.value.minutos}")
                                        },
                                        text = { Text(text = "Añadir narración") }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expandedEdit = false
                                            navController.navigate( "${AppScreens.ScreenEditarMarcador.route}/${equipoLocal.codigo}/${equipoVisitante.codigo}/${partido.value.idPartido}/${partido.value.minutos}/${partido.value.golesLocal}/${partido.value.golesVisitante}/${partido.value.estado == "enJuego"}/${partido.value.estado == "finalizado"}")
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
                                if (partido.value.estado != "finalizado") {
                                    if (partido.value.estado == "nuevo") {
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "enJuego"
                                                actualizarEstado = true

                                                iniciarContador(context, idPartido)
                                            },
                                            text = { Text(text = "Iniciar partido") }
                                        )
                                    }
                                    if (partido.value.estado == "enJuego"){
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "detenido"
                                                actualizarEstado = true

                                                detenerContador(context)
                                            },
                                            text = { Text(text = "Detener partido") }
                                        )
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "descanso"
                                                actualizarEstado = true

                                                detenerContador(context)
                                            },
                                            text = { Text(text = "Descanso") }
                                        )
                                    }
                                    if (partido.value.estado == "descanso" || partido.value.estado == "detenido"){
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "enJuego"
                                                actualizarEstado = true

                                                iniciarContador(context, idPartido)
                                            },
                                            text = { Text(text = "Reanudar") }
                                        )
                                    }
                                    if (partido.value.estado != "nuevo"){
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                nuevoEstado = "finalizado"
                                                resultado =
                                                    if (partido.value.golesLocal > partido.value.golesVisitante) "local" else if (partido.value.golesLocal < partido.value.golesVisitante) "visitante" else "empate"
                                                actualizarEstado = true

                                                detenerContador(context)
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
        BodyContentPartido(it, equipoLocal, equipoVisitante, comp, partido.value, isLoadingPartido.value, navController, idPartido)
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
    navController: NavController,
    idPartido: String
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var fila1 = partido.hora
    var fila2 = partido.fecha
    val comentarios = remember { mutableStateListOf<Comentario>() }
    val isLoadingComentarios = remember { mutableStateOf(true) }
    val eventos = remember { mutableStateListOf<Evento>() }
    val isLoadingEventos = remember { mutableStateOf(true) }
    val comprobarComp = remember { mutableStateOf<String?>(null) }
    val comprobarEquipoLocal = remember { mutableStateOf<String?>(null) }
    val comprobarEquipoVisitante = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    if (partido.estado != "nuevo") {
        fila1 = partido.golesLocal + " - " + partido.golesVisitante
        fila2 = partido.minutos + "'"
        if (partido.estado != "enJuego") {
            fila2 = partido.estado
        }
    }

    LaunchedEffect(Unit) {
        escucharCambiosNarraciones(idPartido, comentarios, isLoadingComentarios)
    }

    LaunchedEffect(Unit) {
        escucharCambiosEventos(idPartido, eventos, isLoadingEventos)
    }

    LaunchedEffect(comprobarComp.value) {
        if (comprobarComp.value != null) {
            if (existeCompeticion(comprobarComp.value)) {
                navController.navigate("${AppScreens.ScreenCompeticion.route}/${comp.codigo}/${comp.creador}/${comp.nombre}/${comp.idDocumento}")
            } else {
                Toast.makeText(context, "competición no encontrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(comprobarEquipoLocal.value) {
        if (comprobarEquipoLocal.value != null) {
            if (existeEquipo(comprobarEquipoLocal.value)) {
                navController.navigate("${AppScreens.ScreenEquipo.route}/${equipoLocal.codigo}/${equipoLocal.creador}/${equipoLocal.equipo}/${equipoLocal.idDocumento}")
            } else {
                Toast.makeText(context, "equipo no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(comprobarEquipoVisitante.value) {
        if (comprobarEquipoVisitante.value != null) {
            if (existeEquipo(comprobarEquipoVisitante.value)) {
                navController.navigate("${AppScreens.ScreenEquipo.route}/${equipoVisitante.codigo}/${equipoVisitante.creador}/${equipoVisitante.equipo}/${equipoVisitante.idDocumento}")
            } else {
                Toast.makeText(context, "equipo no encontrado", Toast.LENGTH_SHORT).show()
            }
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
                        comprobarComp.value = comp.idDocumento
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
                            comprobarEquipoLocal.value = equipoLocal.idDocumento
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
                    Text(text = fila2, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Text(text = partido.fecha, fontSize = 14.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            comprobarEquipoVisitante.value = equipoVisitante.idDocumento
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
                    if(isLoadingEventos.value){
                        Box (
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ){
                            CircularProgressIndicator()
                        }
                    }
                    else if(eventos.isEmpty()){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Aún no existen eventos\nen este partido",
                                fontSize = 22.sp,
                                color = colorScheme.secondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else{
                        MostrarEventos(eventos)
                    }
                } else if (index == 1) {
                    if(isLoadingComentarios.value){
                        Box (
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ){
                            CircularProgressIndicator()
                        }
                    }
                    else if(comentarios.isEmpty()){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Aún no existen comentarios\nen este partido",
                                fontSize = 22.sp,
                                color = colorScheme.secondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else{
                        MostrarComentarios(comentarios)
                    }
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

suspend fun verificarEquiposCompeticion(idPartido: String) {
    val db = FirebaseFirestore.getInstance()

    // Obtén el documento del partido
    val partidoDocumentRef = db.collection("partidos").document(idPartido)
    val partidoDocument = partidoDocumentRef.get().await()

    // Obtén los IDs de los equipos local y visitante, y la competición
    val idLocal = partidoDocument.getString("idLocal") ?: ""
    val idVisitante = partidoDocument.getString("idVisitante") ?: ""
    val idComp = partidoDocument.getString("idComp") ?: ""

    // Verifica si el equipo local existe
    val equipoLocalDocument = db.collection("equipos").document(idLocal).get().await()
    if (!equipoLocalDocument.exists()) {
        // Si el equipo local no existe, actualiza el campo "idLocal" a una cadena vacía
        partidoDocumentRef.update("idLocal", "").await()
    }

    // Verifica si el equipo visitante existe
    val equipoVisitanteDocument = db.collection("equipos").document(idVisitante).get().await()
    if (!equipoVisitanteDocument.exists()) {
        // Si el equipo visitante no existe, actualiza el campo "idVisitante" a una cadena vacía
        partidoDocumentRef.update("idVisitante", "").await()
    }

    // Verifica si la competición existe
    val competicionDocument = db.collection("competiciones").document(idComp).get().await()
    if (!competicionDocument.exists()) {
        // Si la competición no existe, actualiza el campo "idComp" a una cadena vacía
        partidoDocumentRef.update("idComp", "").await()
    }
}

fun escucharCambiosPartido(
    idPartido: String,
    partido: MutableState<Partido>,
    isLoadingPartido: MutableState<Boolean>
) {
    isLoadingPartido.value = true
    try {
        val db = Firebase.firestore

        db.collection("partidos").document(idPartido)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("ScreenParido", "listener cambios partido error", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    partido.value = Partido(
                        idPartido = snapshot.id,
                        competicion = snapshot.getString("idComp") ?: "eliminado",
                        local = snapshot.getString("idLocal") ?: "eliminado",
                        visitante = snapshot.getString("idVisitante") ?: "eliminado",
                        fecha = snapshot.getString("fecha") ?: "",
                        hora = snapshot.getString("hora") ?: "",
                        estado = snapshot.getString("estado") ?: "",
                        golesLocal = snapshot.getLong("golesLocal").toString(),
                        golesVisitante = snapshot.getLong("golesVisitante").toString(),
                        creador = snapshot.getString("creador") ?: "eliminado",
                        minutos = snapshot.getLong("minutos").toString(),
                        ganador = snapshot.getString("ganador") ?: ""
                    )
                }
            }
    } catch (e: Exception){
        Log.w("ScreenParido", "error consulta partido", e)
    } finally {
        isLoadingPartido.value = false
    }
}


suspend fun getEquipoPorId(idPartido: String, isLocal: Boolean): Equipo {
    var consulta = "idLocal"
    if (!isLocal) {
        consulta = "idVisitante"
    }

    val db = FirebaseFirestore.getInstance()
    val partidoDocument = db.collection("partidos").document(idPartido).get().await()
    val idEquipo = partidoDocument.getString(consulta) ?: ""

    if (idEquipo != ""){
        val equipoDocument = db.collection("equipos").document(idEquipo).get().await()
        val idCreador = equipoDocument.getString("creador") ?: ""

        val creadorDocument = db.collection("users").document(idCreador).get().await()
        return Equipo(
            codigo = equipoDocument.getString("codigo")?.uppercase() ?: "",
            equipo = equipoDocument.getString("equipo") ?: "",
            idDocumento = equipoDocument.id,
            creador = creadorDocument.getString("username") ?: ""
        )
    } else{
        return Equipo(
            codigo = "",
            equipo = "eliminado",
            idDocumento = "",
            creador = ""
        )
    }
}

suspend fun getCompeticionPorId(idPartido: String): Comp {
    val db = FirebaseFirestore.getInstance()
    val partidoDocument = db.collection("partidos").document(idPartido).get().await()
    val idCompeticion = partidoDocument.getString("idComp") ?: ""

    if (idCompeticion != ""){
        val idCreador = partidoDocument.getString("creador") ?: ""

        val competicionDocument = db.collection("competiciones").document(idCompeticion).get().await()
        val creadorDocument = db.collection("users").document(idCreador).get().await()
        return Comp(
            codigo = competicionDocument.getString("codigo") ?: "",
            nombre = competicionDocument.getString("competicion") ?: "",
            idDocumento = competicionDocument.id,
            creador = creadorDocument.getString("username") ?: ""
        )
    } else{
        return Comp(
            codigo = "",
            nombre = "eliminado",
            idDocumento = "",
            creador = ""
        )
    }
}

suspend fun funActualizarEstadoPartido(idPartido: String, nuevoEstado: String, resultado: String) {
    val db = FirebaseFirestore.getInstance()
    val ref = db.collection("partidos").document(idPartido)

    val updates = hashMapOf<String, Any>("estado" to nuevoEstado)
    if (resultado != "") updates["ganador"] = resultado

    ref.update(updates).await()
}

fun escucharCambiosNarraciones(
    idPartido: String,
    comentarios: MutableList<Comentario>,
    isLoadingComentarios: MutableState<Boolean>
) {
    isLoadingComentarios.value = true
    try {
        val db = Firebase.firestore

        db.collection("narraciones")
            .whereEqualTo("idPartido", idPartido)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("ScreenParido", "listener cambios narraciones error", error)
                    return@addSnapshotListener
                }

                comentarios.clear()
                if (value != null) {
                    for (doc in value) {
                        val comentario = Comentario(
                            idComentario = doc.id,
                            titulo = doc.getString("titulo") ?: "",
                            texto = doc.getString("texto") ?: "",
                            minuto = (doc.getLong("minuto")?.toString() + "'")
                        )
                        comentarios.add(comentario)
                        comentarios.sortWith(compareBy { it.minuto.replace("'", "").toInt() })
                    }
                }
            }
    } catch (e: Exception){
        Log.w("ScreenParido", "error consulta comentarios", e)
    } finally {
        isLoadingComentarios.value = false
    }
}

fun escucharCambiosEventos(
    idPartido: String,
    eventos: MutableList<Evento>,
    isLoadingEventos: MutableState<Boolean>
) {
    isLoadingEventos.value = true
    try {
        val db = Firebase.firestore

        db.collection("eventos")
            .whereEqualTo("idPartido", idPartido)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("ScreenParido", "listener cambios eventos error", error)
                    return@addSnapshotListener
                }

                eventos.clear()
                if (value != null) {
                    for (doc in value) {
                        val evento = Evento(
                            idEvento = doc.id,
                            titulo = doc.getString("titulo") ?: "",
                            texto = doc.getString("texto") ?: "",
                            minuto = (doc.getLong("minuto")?.toString() + "'"),
                            tipoEquipo = doc.getString("tipoEquipo") ?: "",
                            tipoEvento = doc.getString("tipoEvento") ?: ""
                        )
                        eventos.add(evento)
                        eventos.sortWith(compareBy { it.minuto.replace("'", "").toInt() })
                    }
                }
            }
    } catch (e: Exception){
        Log.w("ScreenParido", "error consulta eventos", e)
    } finally {
        isLoadingEventos.value = false
    }
}

fun iniciarContador(context: Context, idPartido: String) {
    val intent = Intent(context, ActualizarMinutosService::class.java).apply {
        putExtra("idPartido", idPartido)
    }
    context.startService(intent)
}

fun detenerContador(context: Context) {
    val intent = Intent(context, ActualizarMinutosService::class.java)
    context.stopService(intent)
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