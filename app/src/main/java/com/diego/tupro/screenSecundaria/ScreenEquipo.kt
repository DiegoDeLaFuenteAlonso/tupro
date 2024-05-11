package com.diego.tupro.screenSecundaria

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.diego.prueba.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.diego.tupro.screenPrincipal.DibujarColumnaItems
import com.diego.tupro.screenPrincipal.Equipo
import com.diego.tupro.screenPrincipal.ItemBusqueda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenEquipo(
    navController: NavController,
    codigo: String,
    creador: String,
    nombre: String,
    id: String
) {
    val equipo = Equipo(codigo, nombre, id, creador)
    val esFav = remember { mutableStateOf(false) }
    val actualizarFav = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    LaunchedEffect(Unit) {
        esFav.value = existeFavEquipos(equipo.idDocumento, uid)
    }
    LaunchedEffect(actualizarFav.value) {
        if (actualizarFav.value) {
            actualizarFavEquipos(equipo.idDocumento, uid)
            actualizarFav.value = false
            esFav.value = !esFav.value
        }
    }
    Scaffold(
        topBar = {
            Column{
                TopAppBar(
                    title = {
                        Text(text = "Equipo", color = colorScheme.primary, fontSize = 26.sp)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { if(uid != null) actualizarFav.value = true else showDialog.value = true}
                        ) {
                            Icon(
                                if (esFav.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Añadir a favoritos"
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    ) {innerPadding ->
        BodyContentEquipo(innerPadding, navController, equipo)
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Advertencia") },
            text = { Text("Para añadir equipos a favoritos\nes necesario iniciar sesión") },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun BodyContentEquipo(innerPadding: PaddingValues, navController: NavController, equipo: Equipo) {
    //val competiciones = remember { mutableStateListOf(ItemBusqueda("LB", "La Bañeza", "1", "Diego", "Equipo"), ItemBusqueda("LB", "La Bañeza", "1", "Diego", "Equipo")) }
    val competiciones = remember { mutableStateListOf<ItemBusqueda>() }
    val isLoading = remember { mutableStateOf(true) }
    LaunchedEffect(equipo.idDocumento) {
        competiciones.addAll(getCompeticiones(equipo.idDocumento))
        isLoading.value = false
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(Constantes.redondeoBoton))
                        .background(colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        equipo.codigo.uppercase(Locale.ROOT),
                        color = colorScheme.onSecondaryContainer,
                        fontSize = 30.sp
                    )
                }
                Text(text = equipo.equipo + " #" + equipo.idDocumento)
                Text(text = equipo.creador)
            }
        }
        HorizontalDivider()
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 5.dp, bottom = 8.dp)
        ){
            Text(text = "Competiciones", color = colorScheme.primary, fontSize = 20.sp)
        }
        HorizontalDivider()
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
                Text("Este equipo aún no está\nen ninguna competición",
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

suspend fun getCompeticiones(id: String): SnapshotStateList<ItemBusqueda> {
    val db = FirebaseFirestore.getInstance()
    val competiciones = mutableStateListOf<ItemBusqueda>()

    val querySnapshot = db.collection("competiciones")
        .whereArrayContains("equipos", id)
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

suspend fun actualizarFavEquipos(idEquipo: String, uid: String?) = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()

    if (uid != null) {
        val userDocumentRef = db.collection("users").document(uid)

        val userDocument = userDocumentRef.get().await()
        val favEquipos = userDocument.get("favEquipos") as? MutableList<String> ?: mutableListOf()

        if (idEquipo in favEquipos) {
            favEquipos.remove(idEquipo)
        } else {
            favEquipos.add(idEquipo)
        }

        userDocumentRef.update("favEquipos", favEquipos).await()
    }
}

suspend fun existeFavEquipos(id: String, uid: String?): Boolean = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()

    if (uid != null) {
        val userDocument = db.collection("users").document(uid).get().await()
        val favEquipos = userDocument.get("favEquipos") as? List<String> ?: listOf()
        return@withContext id in favEquipos
    } else {
        return@withContext false
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewEquipo1() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenEquipo(navController, "LB", "Diego", "La Bañeza", "1")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkEquipo1() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenEquipo(navController, "", "", "", "")
    }
}