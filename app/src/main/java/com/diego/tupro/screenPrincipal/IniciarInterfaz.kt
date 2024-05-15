package com.diego.tupro.screenPrincipal

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.ui.theme.TuproTheme
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material3.ListItem
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.diego.tupro.Constantes
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IniciarInterfaz(navController: NavController) {
    Scaffold (
        topBar = { BarraSuperior("")},
        bottomBar = { BarraInferior(navController, 0) }
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
                        if (it.tipo == "Equipo") navController.navigate("screen_equipo/${it.codigo}/${it.creador}/${it.nombre}/${it.idDocumento}")
                        else if (it.tipo == "Competicion") navController.navigate("screen_competicion/${it.codigo}/${it.creador}/${it.nombre}/${it.idDocumento}")
                        else if (it.tipo == "Usuario") {
                            val auth = FirebaseAuth.getInstance()
                            val uid = auth.currentUser?.uid
                            if (uid != null){
                                if (uid != it.idDocumento) navController.navigate("screen_perfil/${it.nombre}/${it.idDocumento}")
                                else navController.navigate("item_perfil")
                            }
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
fun BarraInferior(navController: NavController, i: Int) {
    val items = listOf(
        BottomNavigationItem("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavigationItem("Buscar", Icons.Default.SavedSearch, Icons.Outlined.Search),
        BottomNavigationItem("Favoritos", Icons.Default.Favorite, Icons.Default.FavoriteBorder),
        BottomNavigationItem("Perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    // Obtiene la entrada actual en la pila de retroceso
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    // Actualiza el índice del elemento seleccionado en la barra de navegación
    // basándose en la ruta actual
    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            "item_inicio" -> selectedItemIndex = 0
            "item_buscar" -> selectedItemIndex = 1
            "item_favoritos" -> selectedItemIndex = 2
            "item_perfil" -> selectedItemIndex = 3
        }
    }

    NavigationBar {
        items.forEachIndexed() { index, item ->
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