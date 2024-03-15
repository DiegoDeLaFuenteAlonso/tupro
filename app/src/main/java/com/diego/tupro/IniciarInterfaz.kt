package com.diego.tupro

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlin.system.exitProcess

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IniciarInterfaz(navController: NavController) {
    Scaffold (
        bottomBar = {BarraInferior(navController, 0)}
    ){
        // ItemInicio()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    TopAppBar(
        title = { Text("hola") },
    )
}

@Composable
fun BarraInferior(navController: NavController, i: Int) {
    var n = i

    /* BackHandler {
        // Cierra la aplicación
        exitProcess(0)
    }*/
    val items = listOf(
        BottomNavigationItem("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavigationItem("Buscar", Icons.Filled.Search, Icons.Outlined.Search),
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
            "item_perfil" -> selectedItemIndex = 2
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
                    navController.navigate("item_" + item.titulo.toLowerCase())
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