package com.diego.tupro.screenPrincipal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.diego.tupro.Constantes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@Composable
fun ItemBuscar(navController: NavController) {
    Scaffold (
        // containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            Column {
                BarraSuperior("Buscar")
            }
        },
        bottomBar = { BarraInferior(navController = navController, 1)}

    ) { innerPadding ->
        BodyContentBuscar(innerPadding, navController)
    }
}

@Composable
fun BodyContentBuscar(
    innerPaddingValues: PaddingValues,
    navController: NavController
) {
    var textoBuscar by remember { mutableStateOf("") }
    val resultBusqueda = remember { mutableStateListOf<ItemBusqueda>() }

    val isLoading = remember { mutableStateOf(false) }
    val realizarConsulta = remember { mutableStateOf(false) }
    LaunchedEffect(realizarConsulta.value) {
        isLoading.value = true
        textoBuscar = textoBuscar.trim()
        if (realizarConsulta.value && textoBuscar.isNotEmpty()) {
            resultBusqueda.clear()
            if(!textoBuscar.startsWith("#")){
                resultBusqueda.addAll(getEquiposBuscar(textoBuscar))
                resultBusqueda.addAll(getCompeticionesBuscar(textoBuscar))
                resultBusqueda.addAll(getUsuariosBuscar(textoBuscar))
            } else{
                resultBusqueda.addAll(getEquiposPorIdBuscar(textoBuscar.substring(1, textoBuscar.length)))
                resultBusqueda.addAll(getCompeticionesPorIdBuscar(textoBuscar.substring(1, textoBuscar.length)))
            }
        }
        realizarConsulta.value = false
        isLoading.value = false
    }

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
                singleLine = true,
                label = { Text(text = "Buscar")}
            )
            IconButton(
                onClick = {
                    textoBuscar = textoBuscar.trim()
                    if(textoBuscar.isNotEmpty()){
                        realizarConsulta.value = true
                    }
                },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .clip(RoundedCornerShape(Constantes.redondeoBoton))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar")
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        Row(
            modifier = Modifier
                .weight(0.9f)
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
            else if(resultBusqueda.isEmpty()){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Búsqueda sin resultados",
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else{
                DibujarColumnaItems(resultBusqueda, navController)
            }
        }
    }
}

suspend fun getEquiposBuscar(textoBuscar: String): SnapshotStateList<ItemBusqueda> {
    return withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val equipos = mutableStateListOf<ItemBusqueda>()

        val querySnapshotEquipos = db.collection("equipos")
            .orderBy("nombreBusqueda")
            .startAt(textoBuscar.uppercase())
            .endAt(textoBuscar.uppercase() + '\uf8ff')
            .get()
            .await()

        for (document in querySnapshotEquipos.documents) {
            val codigoE = document.getString("codigo") ?: ""
            val nombre = document.getString("equipo") ?: ""
            val idDocumento = document.id
            val creadorId = document.getString("creador") ?: ""

            val userDocument = db.collection("users").document(creadorId).get().await()
            val creadorNombre = userDocument.getString("username") ?: ""

            val equipo = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Equipo")
            equipos.add(equipo)
        }

        equipos
    }
}

suspend fun getCompeticionesBuscar(textoBuscar: String): SnapshotStateList<ItemBusqueda> {
    return withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val competiciones = mutableStateListOf<ItemBusqueda>()

        val querySnapshotCompeticiones = db.collection("competiciones")
            .orderBy("nombreBusqueda")
            .startAt(textoBuscar.uppercase())
            .endAt(textoBuscar.uppercase() + '\uf8ff')
            .get()
            .await()

        for (document in querySnapshotCompeticiones.documents) {
            val codigoE = document.getString("codigo") ?: ""
            val nombre = document.getString("competicion") ?: ""
            val idDocumento = document.id
            val creadorId = document.getString("creador") ?: ""

            val userDocument = db.collection("users").document(creadorId).get().await()
            val creadorNombre = userDocument.getString("username") ?: ""

            val comp = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Competicion")
            competiciones.add(comp)
        }

        competiciones
    }
}

suspend fun getUsuariosBuscar(textoBuscar: String): SnapshotStateList<ItemBusqueda> {
    return withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val usuarios = mutableStateListOf<ItemBusqueda>()

        val querySnapshotUsuarios = db.collection("users")
            .orderBy("usernameBusqueda")
            .startAt(textoBuscar.uppercase())
            .endAt(textoBuscar.uppercase() + '\uf8ff')
            .get()
            .await()

        for (document in querySnapshotUsuarios.documents) {
            val nombre = document.getString("username") ?: ""
            val codigo = nombre.uppercase().substring(0, 1)
            val idDocumento = document.id

            val usuario = ItemBusqueda(codigo, nombre, idDocumento, "", "Usuario")
            usuarios.add(usuario)
        }

        usuarios
    }
}

suspend fun getEquiposPorIdBuscar(textoBuscar: String): SnapshotStateList<ItemBusqueda> {
    return withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val equipos = mutableStateListOf<ItemBusqueda>()

        val querySnapshotEquipos = db.collection("equipos")
            .get()
            .await()

        for (document in querySnapshotEquipos.documents) {
            val idDocumento = document.id
            if (idDocumento.startsWith(textoBuscar)) {
                val codigoE = document.getString("codigo") ?: ""
                val nombre = document.getString("equipo") ?: ""
                val creadorId = document.getString("creador") ?: ""

                val userDocument = db.collection("users").document(creadorId).get().await()
                val creadorNombre = userDocument.getString("username") ?: ""

                val equipo = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Equipo")
                equipos.add(equipo)
            }
        }

        equipos
    }
}

suspend fun getCompeticionesPorIdBuscar(textoBuscar: String): SnapshotStateList<ItemBusqueda> {
    return withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val competiciones = mutableStateListOf<ItemBusqueda>()

        val querySnapshotCompeticiones = db.collection("competiciones")
            .get()
            .await()

        for (document in querySnapshotCompeticiones.documents) {
            val idDocumento = document.id
            if (idDocumento.startsWith(textoBuscar)) {
                val codigoE = document.getString("codigo") ?: ""
                val nombre = document.getString("competicion") ?: ""
                val creadorId = document.getString("creador") ?: ""

                val userDocument = db.collection("users").document(creadorId).get().await()
                val creadorNombre = userDocument.getString("username") ?: ""

                val competicion = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Competicion")
                competiciones.add(competicion)
            }
        }

        competiciones
    }
}

data class ItemBusqueda(
    val codigo: String,
    val nombre: String,
    val idDocumento: String,
    val creador: String,
    val tipo: String
)