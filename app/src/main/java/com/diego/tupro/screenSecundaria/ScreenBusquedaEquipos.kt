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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.Constantes
import com.diego.tupro.screenPrincipal.Equipo
import com.diego.tupro.ui.theme.TuproTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenBusquedaEquipos(navController: NavController, competicion: String?, codigo: String?) {
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
        BodyContentPerfil(innerPadding, competicion, codigo, navController)
    }
}

@Composable
fun BodyContentPerfil(
    innerPaddingValues: PaddingValues,
    competicion: String?,
    codigo: String?,
    navController: NavController
) {
    var textoBuscar by remember { mutableStateOf("") }
    val selecEquiposBusqueda = remember { mutableStateListOf<Equipo>() }
    val resultBusquedaEquipos = remember { mutableStateListOf<Equipo>() }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val db = FirebaseFirestore.getInstance()
    val consultasNombre = remember { mutableStateListOf(1) }
    val consultasID = remember { mutableStateListOf(1) }
    val numeroConsultasNombre = 1
    val numeroConsultasID = 1
    val maxEquipos = remember { mutableIntStateOf(30) }
    var botonesActivos by remember { mutableStateOf(true) }

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
                        val equiposRef = db.collection("equipos")
                        if(!textoBuscar.startsWith("#")){
                            val query = equiposRef
                                .orderBy("nombreBusqueda")
                                .startAt(textoBuscar.uppercase())
                                .endAt(textoBuscar.uppercase() + '\uf8ff')
                            try {
                                query.get().addOnSuccessListener { result ->
                                    Log.w("busqueda_equipos", "Empieza consulta, $textoBuscar")
                                    resultBusquedaEquipos.clear()
                                    consultasNombre.clear()
                                    for (document in result) {
                                        val codigoE = document.getString("codigo") ?: ""
                                        val nombre = document.getString("equipo") ?: ""
                                        val idDocumento = document.id
                                        val creadorId = document.getString("creador") ?: ""

                                        val usersRef = db.collection("users")
                                        usersRef.document(creadorId).get()
                                            .addOnSuccessListener { d ->
                                                if (d != null) {
                                                    Log.d("busqueda_equipos", "DocumentSnapshot data: ${d.data}")
                                                    val creadorNombre = d.getString("username") ?: ""
                                                    val equipo = Equipo(codigoE, nombre, idDocumento, creadorNombre)
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
                                    consultasNombre.add(1)
                                }.addOnCanceledListener {
                                    Log.w("busqueda_equipos", "consulta cancelada")
                                    resultBusquedaEquipos.clear()
                                }
                            } catch (exception: Exception) {
                                Log.w("busqueda_equipos", "Error: ", exception)
                            }
                        } else{
                            val textoBuscarConsulta = textoBuscar.substring(1,textoBuscar.length)
                            val usersRef = db.collection("users")
                            try{
                                equiposRef.get().addOnSuccessListener { result ->
                                    resultBusquedaEquipos.clear()
                                    consultasID.clear()
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
                                                        val equipo = Equipo(codigoE, nombre, idDocumento, creadorNombre)
                                                        resultBusquedaEquipos.add(equipo)
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
                                    consultasID.add(1)
                                }.addOnCanceledListener {
                                    Log.w("busqueda_equipos_id", "consulta cancelada")
                                }
                            } catch (exception: Exception) {
                                Log.w("busqueda_id", "Error: ", exception)
                            }
                        }
                    }
                },
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
                .weight(0.75f)
                .fillMaxWidth()
        ) {
            if(consultasNombre.size < numeroConsultasNombre && consultasID.size < numeroConsultasID){
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
                                        fontSize = 20.sp
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
        Row (
            modifier = Modifier
                .weight(0.15f)
                .fillMaxWidth()
                .padding(start = 20.dp, top = 5.dp, end = 20.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Column (
                modifier = Modifier.fillMaxWidth()
            ){
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Constantes.redondeoBoton))
                        .background(if (selecEquiposBusqueda.size > maxEquipos.intValue) colorScheme.errorContainer else colorScheme.secondaryContainer)
                        .align(Alignment.CenterHorizontally)
                ){
                    Text(
                        text = selecEquiposBusqueda.size.toString() + "/" + maxEquipos.intValue.toString(),
                        color = if (selecEquiposBusqueda.size > maxEquipos.intValue) colorScheme.onErrorContainer else colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(start = 5.dp, end = 5.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val user = Firebase.auth.currentUser
                        val uid = user?.uid
                        botonesActivos = false

                        val counterRef = db.collection("counters").document("equiposCounter")
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(counterRef)
                            val newCounter = snapshot.getLong("counter")?.plus(1) ?: 0
                            transaction.update(counterRef, "counter", newCounter)

                            val competicionesRef = db.collection("competiciones").document(newCounter.toString())
                            val equipos = selecEquiposBusqueda.map { it.idDocumento }
                            val competicionData = hashMapOf(
                                "competicion" to competicion,
                                "codigo" to codigo,
                                "nombreBusqueda" to (competicion?.uppercase() ?: ""),
                                "creador" to uid,
                                "equipos" to equipos
                            )
                            transaction.set(competicionesRef, competicionData)
                        }.addOnSuccessListener {
                            Log.d("TAG", "Transaction success!")
                            Constantes.reiniciarNavegacion(navController)
                        }.addOnFailureListener { e ->
                            Log.w("TAG", "Transaction failure.", e)
                        }
                        botonesActivos = true
                    },
                    enabled = selecEquiposBusqueda.size > 1 && selecEquiposBusqueda.size <= maxEquipos.intValue && botonesActivos,
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(Constantes.redondeoBoton)
                ) {
                    Text(text = "Crear competición")
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
        ScreenBusquedaEquipos(navController, "", "")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkBusquedaEquipos() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenBusquedaEquipos(navController, "", "")
    }
}