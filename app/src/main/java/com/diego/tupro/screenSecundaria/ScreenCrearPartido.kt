package com.diego.tupro.screenSecundaria

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.diego.tupro.screenPrincipal.ItemBusqueda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCrearPartido(navController: NavController, idComp: String) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    title = {
                        Text(
                            text = "Selecciona los equipos",
                            fontSize = 26.sp,
                            //fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                        )
                    }
                )
                HorizontalDivider(thickness = 1.dp)
            }

        }
    ) {
        BodyContentScreenCrearPartido(it, idComp, navController)
    }
}

@Composable
fun BodyContentScreenCrearPartido(
    paddingValues: PaddingValues,
    idComp: String,
    navController: NavController
) {
    val equiposComp = remember { mutableStateListOf<ItemBusqueda>() }
    val isLoading = remember { mutableStateOf(true)}
    var equipoLocal by remember { mutableStateOf<ItemBusqueda?>(null) }
    var equipoVisitante by remember { mutableStateOf<ItemBusqueda?>(null) }

    LaunchedEffect(Unit) {
        equiposComp.addAll(getEquiposCompeticion(idComp))
        isLoading.value = false
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxSize()
                .padding(start = 50.dp, end = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                val seleccionado = equipoLocal != null
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(Constantes.redondeoBoton))
                        .background(if (seleccionado) colorScheme.secondaryContainer else colorScheme.surfaceVariant)
                        .border(
                            width = 1.dp,
                            color = if (seleccionado) colorScheme.secondaryContainer else colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(Constantes.redondeoBoton)
                        )
                        .clickable{ if (seleccionado) equipoLocal = null },
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        text = if (seleccionado) equipoLocal!!.codigo.uppercase() else "L",
                        color = if (seleccionado) colorScheme.onSecondaryContainer else colorScheme.onSurfaceVariant,
                        fontSize = if (seleccionado) 24.sp else 38.sp
                    )
                }
                if(seleccionado){
                    Text(text = equipoLocal!!.nombre + " #" + equipoLocal!!.idDocumento)
                    Text(text = equipoLocal!!.creador)
                }
            }

            Column {
                val seleccionado = equipoVisitante != null
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.43f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(Constantes.redondeoBoton))
                        .background(if (seleccionado) colorScheme.secondaryContainer else colorScheme.surfaceVariant)
                        .border(
                            width = 1.dp,
                            color = if (seleccionado) colorScheme.secondaryContainer else colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(Constantes.redondeoBoton)
                        )
                        .clickable{ if (seleccionado) equipoVisitante = null },
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        text = if (seleccionado) equipoVisitante!!.codigo.uppercase() else "V",
                        color = if (seleccionado) colorScheme.onSecondaryContainer else colorScheme.onSurfaceVariant,
                        fontSize = if (seleccionado) 24.sp else 38.sp
                    )
                }
                if(seleccionado){
                    Text(text = equipoVisitante!!.nombre + " #" + equipoVisitante!!.idDocumento)
                    Text(text = equipoVisitante!!.creador)
                }
            }
        }
        HorizontalDivider(thickness = 1.dp)
        Row(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(isLoading.value){
                Box (
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ){
                    CircularProgressIndicator()
                }
            }
            else if(equiposComp.isEmpty()){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Competición sin equipos",
                        fontSize = 22.sp,
                        color = colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(equiposComp) {
                        val seleccionado = it == equipoLocal || it == equipoVisitante
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
                                        it.codigo.uppercase(Locale.ROOT),
                                        color = if (seleccionado) colorScheme.onTertiaryContainer else colorScheme.onSecondaryContainer,
                                        fontSize = 20.sp
                                    )
                                }
                            },
                            headlineContent = { Text(it.nombre) },
                            supportingContent = { if(it.tipo != "Usuario") Text("#" + it.idDocumento) },
                            trailingContent = { if(it.tipo != "Usuario") Text(it.tipo + ": " + it.creador) else Text(it.tipo)},
                            modifier = Modifier
                                .clickable {
                                    when {
                                        !seleccionado && equipoLocal == null -> equipoLocal = it
                                        !seleccionado && equipoVisitante == null -> equipoVisitante = it
                                        seleccionado && equipoLocal == it -> equipoLocal = null
                                        seleccionado && equipoVisitante == it -> equipoVisitante = null
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
        }
        HorizontalDivider(thickness = 1.dp)
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .padding(start = 20.dp, top = 5.dp, end = 20.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    navController.navigate("screen_seleccionar_fecha/$idComp/${equipoLocal?.idDocumento}/${equipoVisitante?.idDocumento}")
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(Constantes.redondeoBoton),
                enabled = equipoLocal != null && equipoVisitante != null
            ) {
                Text(text = "Continuar")
            }
        }
    }
}

suspend fun getEquiposCompeticion(compId: String): SnapshotStateList<ItemBusqueda> {
    val db = FirebaseFirestore.getInstance()
    val compDocument = db.collection("competiciones").document(compId).get().await()
    val equipos = compDocument.get("equipos") as List<String>

    val equiposComp = SnapshotStateList<ItemBusqueda>()
    for (equipoId in equipos) {
        val equipoDocument = db.collection("equipos").document(equipoId).get().await()

        val codigoE = equipoDocument.getString("codigo") ?: ""
        val nombre = equipoDocument.getString("equipo") ?: ""
        val idDocumento = equipoDocument.id
        val creadorId = equipoDocument.getString("creador") ?: ""

        val creadorDocument = db.collection("users").document(creadorId).get().await()
        val creadorNombre = creadorDocument.getString("username") ?: ""

        val equipo = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Equipo")
        equiposComp.add(equipo)
    }
    return equiposComp
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewScreenCrearPartido() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenCrearPartido(navController, "")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkScreenCrearPartido() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenCrearPartido(navController, "")
    }
}