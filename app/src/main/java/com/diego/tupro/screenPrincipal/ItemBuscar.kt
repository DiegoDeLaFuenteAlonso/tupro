package com.diego.tupro.screenPrincipal

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.diego.tupro.Constantes
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


@Composable
fun ItemBuscar(navController: NavController) {
    Scaffold (
        // containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            Column {
                BarraSuperior("")
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
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val db = FirebaseFirestore.getInstance()
    val isLoading = remember { mutableStateOf(false) }

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
                        isLoading.value = true
                        val equiposRef = db.collection("equipos")
                        val competicionesRef = db.collection("competiciones")
                        val usersRef = db.collection("users")

                        if(!textoBuscar.startsWith("#")) {
                            val queryEquipos = equiposRef
                                .orderBy("nombreBusqueda")
                                .startAt(textoBuscar.uppercase())
                                .endAt(textoBuscar.uppercase() + '\uf8ff')

                            val queryCompeticiones = competicionesRef
                                .orderBy("nombreBusqueda")
                                .startAt(textoBuscar.uppercase())
                                .endAt(textoBuscar.uppercase() + '\uf8ff')

                            val queryUsers = usersRef
                                .orderBy("usernameBusqueda")
                                .startAt(textoBuscar.uppercase())
                                .endAt(textoBuscar.uppercase() + '\uf8ff')

                            try {
                                queryEquipos.get().addOnSuccessListener { result ->
                                    Log.w("busqueda_equipos", "Empieza consulta, $textoBuscar")
                                    resultBusqueda.clear()
                                    for (document in result) {
                                        val codigoE = document.getString("codigo") ?: ""
                                        val nombre = document.getString("equipo") ?: ""
                                        val idDocumento = document.id
                                        val creadorId = document.getString("creador") ?: ""

                                        usersRef.document(creadorId).get()
                                            .addOnSuccessListener { d ->
                                                if (d != null) {
                                                    Log.d("busqueda_equipos", "DocumentSnapshot data: ${d.data}")
                                                    val creadorNombre = d.getString("username") ?: ""
                                                    val equipo = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Equipo")
                                                    resultBusqueda.add(equipo)
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
                                    Log.w("busqueda_equipos", "Error getting documents: ", exception
                                    )
                                }.addOnCompleteListener {
                                    Log.w("busqueda_equipos", "consulta realizada bien")
                                    softwareKeyboardController?.hide()
                                }.addOnCanceledListener {
                                    Log.w("busqueda_equipos", "consulta cancelada")
                                    resultBusqueda.clear()
                                }

                                queryCompeticiones.get().addOnSuccessListener { result ->
                                    Log.w("busqueda_competiciones", "Empieza consulta, $textoBuscar")
                                    for (document in result) {
                                        val codigoE = document.getString("codigo") ?: ""
                                        val nombre = document.getString("competicion") ?: ""
                                        val idDocumento = document.id
                                        val creadorId = document.getString("creador") ?: ""

                                        usersRef.document(creadorId).get()
                                            .addOnSuccessListener { d ->
                                                if (d != null) {
                                                    Log.d("busqueda_competiciones", "DocumentSnapshot data: ${d.data}")
                                                    val creadorNombre =
                                                        d.getString("username") ?: ""
                                                    val comp = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Competición")
                                                    resultBusqueda.add(comp)
                                                } else {
                                                    Log.d("busqueda_competiciones", "No such document")
                                                }
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.d("busqueda_competiciones", "get failed with ", exception)
                                            }
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d("busqueda_competiciones", "Query completed successfully")
                                                }
                                            }
                                            .addOnCanceledListener {
                                                Log.d("busqueda_competiciones", "Query was cancelled")
                                            }
                                    }
                                    Log.w("busqueda_competiciones", "Acaba consulta, $textoBuscar")
                                }.addOnFailureListener { exception ->
                                    Log.w(
                                        "busqueda_competiciones",
                                        "Error getting documents: ",
                                        exception
                                    )
                                }.addOnCompleteListener {
                                    Log.w("busqueda_competiciones", "consulta realizada bien")
                                    softwareKeyboardController?.hide()
                                }.addOnCanceledListener {
                                    Log.w("busqueda_competiciones", "consulta cancelada")
                                    resultBusqueda.clear()
                                }

                                queryUsers.get().addOnSuccessListener { result ->
                                    Log.w("busqueda_usuarios", "Empieza consulta, $textoBuscar")
                                    for (document in result) {
                                        val nombre = document.getString("username") ?: ""
                                        val codigo = nombre.uppercase().substring(0, 1)
                                        val idDocumento = document.id
                                        resultBusqueda.add(ItemBusqueda(codigo, nombre, idDocumento, "", "Usuario"))
                                    }
                                    Log.w("busqueda_usuarios", "Acaba consulta, $textoBuscar")
                                }.addOnFailureListener { exception ->
                                    Log.w("busqueda_usuarios", "Error getting documents: ", exception)
                                }.addOnCompleteListener {
                                    Log.w("busqueda_usuarios", "consulta realizada bien")
                                    softwareKeyboardController?.hide()
                                }.addOnCanceledListener {
                                    Log.w("busqueda_usuarios", "consulta cancelada")
                                    resultBusqueda.clear()
                                }

                            } catch (exception: Exception) {
                                Log.w("busqueda_nombre", "Error: ", exception)
                            }
                        } else{
                            val textoBuscarConsulta = textoBuscar.substring(1,textoBuscar.length)
                            try{
                                equiposRef.get().addOnSuccessListener { result ->
                                    for (document in result) {
                                        val idDocumento = document.id
                                        if (idDocumento.startsWith(textoBuscarConsulta)) {
                                            val codigoE = document.getString("codigo") ?: ""
                                            val nombre = document.getString("equipo") ?: ""
                                            val creadorId = document.getString("creador") ?: ""

                                            usersRef.document(creadorId).get()
                                                .addOnSuccessListener { d ->
                                                    if (d != null) {
                                                        Log.d("busqueda_equipos_id", "DocumentSnapshot data: ${d.data}")
                                                        val creadorNombre = d.getString("username") ?: ""
                                                        val equipo = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Equipo")
                                                        resultBusqueda.add(equipo)
                                                    } else {
                                                        Log.d("busqueda_equipos_id", "No such document")
                                                    }
                                                }
                                                .addOnFailureListener { exception ->
                                                    Log.d("busqueda_equipos_id", "get failed with ", exception)
                                                }
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Log.d("busqueda_equipos_id", "Query completed successfully")
                                                    }
                                                }
                                                .addOnCanceledListener {
                                                    Log.d("busqueda_equipos_id", "Query was cancelled")
                                                }
                                        }
                                    }
                                }.addOnFailureListener { exception ->
                                    Log.w("busqueda_equipos_id", "Error getting documents: ", exception)
                                }.addOnCompleteListener {
                                    Log.w("busqueda_equipos_id", "consulta realizada bien")
                                    softwareKeyboardController?.hide()
                                }.addOnCanceledListener {
                                    Log.w("busqueda_equipos_id", "consulta cancelada")
                                    resultBusqueda.clear()
                                }

                                competicionesRef.get().addOnSuccessListener { result ->
                                    for (document in result) {
                                        val idDocumento = document.id
                                        if (idDocumento.startsWith(textoBuscarConsulta)) {
                                            val codigoE = document.getString("codigo") ?: ""
                                            val nombre = document.getString("competicion") ?: ""
                                            val creadorId = document.getString("creador") ?: ""

                                            usersRef.document(creadorId).get()
                                                .addOnSuccessListener { d ->
                                                    if (d != null) {
                                                        Log.d("busqueda_competiciones_id", "DocumentSnapshot data: ${d.data}")
                                                        val creadorNombre = d.getString("username") ?: ""
                                                        val equipo = ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Competicion")
                                                        resultBusqueda.add(equipo)
                                                    } else {
                                                        Log.d("busqueda_competiciones_id", "No such document")
                                                    }
                                                }
                                                .addOnFailureListener { exception ->
                                                    Log.d("busqueda_competiciones_id", "get failed with ", exception)
                                                }
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Log.d("busqueda_competiciones_id", "Query completed successfully")
                                                    }
                                                }
                                                .addOnCanceledListener {
                                                    Log.d("busqueda_competiciones_id", "Query was cancelled")
                                                }
                                        }
                                    }
                                }.addOnFailureListener { exception ->
                                    Log.w("busqueda_competiciones_id", "Error getting documents: ", exception)
                                }.addOnCompleteListener {
                                    Log.w("busqueda_competiciones_id", "consulta realizada bien")
                                    softwareKeyboardController?.hide()
                                }.addOnCanceledListener {
                                    Log.w("busqueda_competiciones_id", "consulta cancelada")
                                    resultBusqueda.clear()
                                }
                            } catch (exception: Exception) {
                                Log.w("busqueda_id", "Error: ", exception)
                            }
                        }
                        isLoading.value = false
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
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center

                                ) {
                                    Text(
                                        it.codigo.uppercase(Locale.ROOT),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontSize = 20.sp
                                    )
                                }
                            },
                            headlineContent = { Text(it.nombre) },
                            supportingContent = { if(!esUser) Text("#" + it.idDocumento) },
                            trailingContent = { if(!esUser) Text(it.tipo + ": " + it.creador) else Text(it.tipo)},
                            modifier = Modifier
                                .clickable {

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

data class ItemBusqueda(
    val codigo: String,
    val nombre: String,
    val idDocumento: String,
    val creador: String,
    val tipo: String
)