package com.diego.tupro.screenSecundaria

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.prueba.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.diego.tupro.screenPrincipal.Equipo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenBusquedaEquipos(navController: NavController, competicion: String?) {

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Añade equipos a tu competición") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
                HorizontalDivider(thickness = 1.dp)
            }
        }
    ) { innerPadding ->
        BodyContentPerfil(innerPadding, competicion)
    }

}

@Composable
fun BodyContentPerfil(innerPaddingValues: PaddingValues, competicion: String?) {
    var textoBuscar by remember { mutableStateOf("") }
    val selecEquiposBusqueda = remember { mutableStateListOf<Equipo>() }
    val resultBusquedaEquipos = remember { mutableStateListOf<Equipo>() }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val db = FirebaseFirestore.getInstance()
    val isLoading = remember { mutableStateOf(false) }
    /*
    val selecEquiposBusqueda = listOf(
        Equipo("lb", "la bañeza", "1", "diego"),
        Equipo("lb", "la bañeza", "1", "diego"),
        Equipo("lb", "la bañeza", "1", "diego"),
        Equipo("lb", "la bañeza", "1", "diego"),
        Equipo("lb", "la bañeza", "1", "diego")
    )
    val resultBusquedaEquipos = listOf(
        Equipo("lb", "la bañeza", "1", "diego"),
        Equipo("lb", "la bañeza", "1", "diego"),
        Equipo("lb", "la bañeza", "1", "diego"),
        Equipo("lb", "la bañeza", "1", "diego"),
        Equipo("lb", "la bañeza", "1", "diego")
    )*/

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPaddingValues)
    ) {
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .padding(start = 20.dp, top = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                value = textoBuscar,
                onValueChange = { textoBuscar = it },
                singleLine = true
            )
            IconButton(
                onClick = {
                    textoBuscar = textoBuscar.trim()
                    if(textoBuscar.isNotEmpty()){
                        val equiposRef = db.collection("equipos")
                        val query = equiposRef
                            .orderBy("nombreBusqueda")
                            .startAt(textoBuscar.uppercase())
                            .endAt(textoBuscar.uppercase() + '\uf8ff')
                        try {
                            isLoading.value = true
                            query.get().addOnSuccessListener { result ->
                                Log.w("busqueda_equipos", "Empieza consulta, $textoBuscar")
                                resultBusquedaEquipos.clear()
                                for (document in result) {
                                    val codigo = document.getString("codigo") ?: ""
                                    val nombre = document.getString("equipo") ?: ""
                                    val idDocumento = document.id
                                    val creadorId = document.getString("creador") ?: ""

                                    val usersRef = db.collection("users")
                                    usersRef.document(creadorId).get()
                                        .addOnSuccessListener { d ->
                                            if (d != null) {
                                                Log.d("busqueda_equipos", "DocumentSnapshot data: ${d.data}")
                                                val creadorNombre = d.getString("username") ?: ""
                                                val equipo = Equipo(codigo, nombre, idDocumento, creadorNombre)
                                                resultBusquedaEquipos.add(equipo)
                                            } else {
                                                Log.d("busqueda_equipos", "No such document")
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.d("busqueda_equipos", "get failed with ", exception)
                                        }
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d("busqueda_equipos", "Query completed successfully")
                                            }
                                        }
                                        .addOnCanceledListener {
                                            Log.d("busqueda_equipos", "Query was cancelled")
                                        }
                                }
                                Log.w("busqueda_equipos", "Acaba consulta, $textoBuscar")
                            }.addOnFailureListener { exception ->
                                Log.w("busqueda_equipos", "Error getting documents: ", exception)
                            }.addOnCompleteListener {
                                Log.w("busqueda_equipos", "consulta realizada bien")
                                softwareKeyboardController?.hide()
                            }.addOnCanceledListener {
                                Log.w("busqueda_equipos", "consulta cancelada")
                                resultBusquedaEquipos.clear()
                            }
                        } catch (exception: Exception) {
                            Log.w("busqueda_equipos", "Error: ", exception)
                        }
                        isLoading.value = false
                    }
                },
                //shape = RoundedCornerShape(Constantes.redondeoBoton),
                modifier = Modifier
                    .padding(all = 8.dp)
                    .clip(RoundedCornerShape(Constantes.redondeoBoton))
                    .background(colorScheme.primaryContainer)
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar")
            }
        }

        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(all = 5.dp)
            ) {
                items(selecEquiposBusqueda) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Constantes.redondeoBoton))
                            .background(colorScheme.secondaryContainer)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        ) {
                            Text(text = "#" + it.idDocumento, color = colorScheme.onSecondaryContainer)
                            Text(text = "\t" + it.equipo + "\t", color = colorScheme.onSecondaryContainer)
                            Icon(
                                Icons.Default.Cancel, contentDescription = "Borrar",
                                modifier = Modifier
                                    .clickable {
                                        selecEquiposBusqueda.remove(it)
                                    }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        Row(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth()
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
            else if(resultBusquedaEquipos.isEmpty()){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Búsqueda sin resultados",
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
                    items(resultBusquedaEquipos) { equipo ->
                        val seleccionado = selecEquiposBusqueda.contains(equipo)
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
                                        fontSize = 20.sp,
                                        //fontWeight = FontWeight.Bold,
                                    )
                                }
                            },
                            headlineContent = { Text(equipo.equipo) },
                            supportingContent = { Text("#" + equipo.idDocumento) },
                            trailingContent = { Text(equipo.creador) },
                            modifier = Modifier
                                .clickable {
                                    if(seleccionado) selecEquiposBusqueda.remove(equipo) else selecEquiposBusqueda.add(equipo)
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
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewBusquedaEquipo() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenBusquedaEquipos(navController, "")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkBusquedaEquipos() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenBusquedaEquipos(navController, "")
    }
}