package com.diego.tupro.screenSecundaria

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.diego.tupro.screenPrincipal.ItemBusqueda
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSeleccionarFecha(navController: NavController, idComp: String, idLocal: String, idVisitante: String){
    Scaffold (
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
                            text = "Selecciona fecha y hora",
                            fontSize = 26.sp,
                            //fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                )
                HorizontalDivider(thickness = 1.dp)
            }

        }
    ){
        BodyContentScreenSeleccionarFecha(it, navController, idComp, idLocal, idVisitante)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentScreenSeleccionarFecha(
    paddingValues: PaddingValues,
    navController: NavController,
    idComp: String,
    idLocal: String,
    idVisitante: String
) {
    var date by remember { mutableStateOf<String?>(null) }
    val openDialogFecha = remember { mutableStateOf(false) }
    val stateFecha = rememberDatePickerState()

    var hora by remember { mutableStateOf<String?>(null) }
    val openDialogHora = remember { mutableStateOf(false) }

    val isLoading = remember { mutableStateOf(true) }
    var equipoLocal by remember { mutableStateOf<ItemBusqueda?>(null) }
    var equipoVisitante by remember { mutableStateOf<ItemBusqueda?>(null) }
    val crearPartido = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isLoading.value = false
        equipoLocal = getEquipo(idLocal)
        equipoVisitante = getEquipo(idVisitante)
    }
    
    LaunchedEffect(crearPartido.value) {
        if (crearPartido.value){
            isLoading.value = true
            if (hora?.let { date?.let { it1 -> crearPartido(idComp, idLocal, idVisitante, it1, it) } } == true){
                Constantes.reiniciarNavegacion(navController)
            }
            isLoading.value = false
            crearPartido.value = false
        }
    }

    val stateHora = rememberTimePickerState()

    if (openDialogFecha.value){
        DatePickerDialog(
            onDismissRequest = {
                openDialogFecha.value = false
            },
            confirmButton = {
                Button(onClick = { openDialogFecha.value = false }) {
                    Text(text = "Confirmar")
                    date = stateFecha.selectedDateMillis?.let { millisToDate(it) }
                }
            }
        ) {
            DatePicker(state = stateFecha)
        }
    }

    if (openDialogHora.value){
        DatePickerDialog(
            onDismissRequest = {
                openDialogHora.value = false
            },
            confirmButton = {
                Button(onClick = { openDialogHora.value = false }) {
                    Text(text = "Confirmar")
                    val hour = String.format("%02d", stateHora.hour)
                    val minute = String.format("%02d", stateHora.minute)
                    hora = "$hour:$minute"
                }
            }
        ) {
            TimePicker(state = stateHora)
        }
    }
    if(isLoading.value){
        Box (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ){
            CircularProgressIndicator()
        }
    }
    else{
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ){
            Row (
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize()
                    .padding(start = 50.dp, end = 50.dp, top = 20.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
            ){
                Column {
                    val seleccionado = equipoLocal != null
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(Constantes.redondeoBoton))
                            .background(if (seleccionado) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (seleccionado) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(Constantes.redondeoBoton)
                            ),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            text = if (seleccionado) equipoLocal!!.codigo.uppercase() else "L",
                            color = if (seleccionado) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
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
                            .background(if (seleccionado) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (seleccionado) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(Constantes.redondeoBoton)
                            ),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            text = if (seleccionado) equipoVisitante!!.codigo.uppercase() else "V",
                            color = if (seleccionado) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
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
            Column (
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth()
                    .padding(start = 60.dp, end = 60.dp, top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = date ?: "",
                        onValueChange = { },
                        label = { Text("Fecha") },
                        readOnly = true,
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxWidth(),
                        //textAlign = TextAlign.Center
                        //enabled = false
                    )

                    IconButton(
                        onClick = { openDialogFecha.value = true },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .clip(RoundedCornerShape(Constantes.redondeoBoton))
                            .background(MaterialTheme.colorScheme.primaryContainer)

                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar fecha")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = hora ?: "",
                        onValueChange = { },
                        label = { Text("Hora") },
                        readOnly = true,
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxWidth()
                    )

                    IconButton(
                        onClick = { openDialogHora.value = true },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .clip(RoundedCornerShape(Constantes.redondeoBoton))
                            .background(MaterialTheme.colorScheme.primaryContainer)

                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar hora")
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
                        crearPartido.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(Constantes.redondeoBoton),
                    enabled = !isLoading.value && date != null && hora != null
                ) {
                    Text(text = "Crear partido")
                }
            }
        }
    }
}

suspend fun getEquipo(id: String): ItemBusqueda {
    val db = FirebaseFirestore.getInstance()
    val equipoDocument = db.collection("equipos").document(id).get().await()
    val codigoE = equipoDocument.getString("codigo") ?: ""
    val nombre = equipoDocument.getString("equipo") ?: ""
    val idDocumento = equipoDocument.id
    val creadorId = equipoDocument.getString("creador") ?: ""

    val creadorDocument = db.collection("users").document(creadorId).get().await()
    val creadorNombre = creadorDocument.getString("username") ?: ""
    
    return ItemBusqueda(codigoE, nombre, idDocumento, creadorNombre, "Equipo")
}

suspend fun crearPartido(idComp: String, idLocal: String, idVisitante: String, fecha: String, hora: String): Boolean {
    val db = Firebase.firestore
    val auth = Firebase.auth

    return try {
        // Obtén el contador actual
        val counterRef = db.collection("counters").document("equiposCounter")
        val counterSnapshot = counterRef.get().await()
        val currentCounter = counterSnapshot.getLong("counter") ?: 0

        // Incrementa el contador
        val newCounter = currentCounter + 1
        counterRef.update("counter", newCounter)

        // Crea el nuevo partido
        val partido = hashMapOf(
            "creador" to auth.currentUser?.uid,
            "idComp" to idComp,
            "idLocal" to idLocal,
            "idVisitante" to idVisitante,
            "golesLocal" to 0,
            "golesVisitante" to 0,
            "estado" to "nuevo",
            "fecha" to fecha,
            "hora" to hora
        )

        // Guarda el nuevo partido en la colección "partidos"
        db.collection("partidos").document(newCounter.toString()).set(partido).await()

        true  // Devuelve true si la operación se completó con éxito
    } catch (e: Exception) {
        false  // Devuelve false si ocurrió una excepción
    }
}


fun millisToDate(millis: Long): String {
    val date = Date(millis)
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(date)
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewScreenSeleccionarFecha() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenSeleccionarFecha(navController, "", "", "")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkScreenSeleccionarFecha() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenSeleccionarFecha(navController, "", "", "")
    }
}