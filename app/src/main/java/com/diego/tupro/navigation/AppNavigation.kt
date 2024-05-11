package com.diego.tupro.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.diego.tupro.screenPrincipal.BarraInferior
import com.diego.tupro.screenPrincipal.ItemBuscar
import com.diego.tupro.screenPrincipal.ItemFavoritos
import com.diego.tupro.screenPrincipal.ItemInicio
import com.diego.tupro.screenPrincipal.ItemPerfil
import com.diego.tupro.screenSecundaria.ScreenBusquedaEquipos
import com.diego.tupro.screenSecundaria.ScreenCompeticion
import com.diego.tupro.screenSecundaria.ScreenCrearCompeticion
import com.diego.tupro.screenSecundaria.ScreenCrearEquipo
import com.diego.tupro.screenSecundaria.ScreenEquipo
import com.diego.tupro.screenSecundaria.ScreenPartido
import com.diego.tupro.screenSecundaria.ScreenPerfil
import com.diego.tupro.screenSecundaria.ScreenRegistro
import com.diego.tupro.screenSecundaria.ScreenSesion
import com.diego.tupro.screenSecundaria.ScreenVerificacion

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val mostrarBarra = remember { mutableStateOf(false) }
    Scaffold (
        bottomBar =  { if (mostrarBarra.value) BarraInferior(navController, 0) }
    ){

        NavHost(navController, AppScreens.ItemInicio.route){
            composable(AppScreens.ItemInicio.route){
                mostrarBarra.value = true
                ItemInicio(navController)
            }
            composable(AppScreens.ItemBuscar.route){
                mostrarBarra.value = true
                ItemBuscar(navController)
            }
            composable(AppScreens.ItemPerfil.route){
                mostrarBarra.value = true
                ItemPerfil(navController)
            }
            composable(AppScreens.ScreenPartido.route) {
                mostrarBarra.value = false
                ScreenPartido(navController)
            }
            composable(AppScreens.ScreenSesion.route) {
                mostrarBarra.value = false
                ScreenSesion(navController)
            }
            composable(AppScreens.ScreenRegistro.route) {
                mostrarBarra.value = false
                ScreenRegistro(navController)
            }
            composable(AppScreens.ScreenVerificacion.route) {
                mostrarBarra.value = false
                ScreenVerificacion(navController)
            }
            composable(AppScreens.ScreenCrearEquipo.route) {
                mostrarBarra.value = false
                ScreenCrearEquipo(navController)
            }
            composable(AppScreens.ScreenCrearCompeticion.route) {
                mostrarBarra.value = false
                ScreenCrearCompeticion(navController)
            }
            composable(
                route = AppScreens.ScreenBusquedaEquipos.route + "/{comp}/{codigo}",
                arguments = listOf(
                    navArgument("comp") { type = NavType.StringType },
                    navArgument("codigo") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val comp = backStackEntry.arguments?.getString("comp")
                val codigo = backStackEntry.arguments?.getString("codigo")
                mostrarBarra.value = false
                ScreenBusquedaEquipos(navController, comp, codigo)
            }
            composable(
                route = AppScreens.ScreenEquipo.route + "/{codigo}/{creador}/{equipo}/{id}",
                arguments = listOf(
                    navArgument("codigo") { type = NavType.StringType },
                    navArgument("creador") { type = NavType.StringType },
                    navArgument("equipo") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val codigo = backStackEntry.arguments?.getString("codigo")
                val creador = backStackEntry.arguments?.getString("creador")
                val equipo = backStackEntry.arguments?.getString("equipo")
                val id = backStackEntry.arguments?.getString("id")
                if (codigo != null) {
                    if (creador != null) {
                        if (equipo != null) {
                            if (id != null) {
                                mostrarBarra.value = false
                                ScreenEquipo(navController, codigo, creador, equipo, id)
                            }
                        }
                    }
                }
            }
            composable(
                route = AppScreens.ScreenCompeticion.route + "/{codigo}/{creador}/{competicion}/{id}",
                arguments = listOf(
                    navArgument("codigo") { type = NavType.StringType },
                    navArgument("creador") { type = NavType.StringType },
                    navArgument("competicion") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val codigo = backStackEntry.arguments?.getString("codigo")
                val creador = backStackEntry.arguments?.getString("creador")
                val competicion = backStackEntry.arguments?.getString("competicion")
                val id = backStackEntry.arguments?.getString("id")
                if (codigo != null) {
                    if (creador != null) {
                        if (competicion != null) {
                            if (id != null) {
                                mostrarBarra.value = false
                                ScreenCompeticion(navController, codigo, creador, competicion, id)
                            }
                        }
                    }
                }
            }
            composable(
                route = AppScreens.ScreenPerfil.route + "/{nombre}/{id}",
                arguments = listOf(
                    navArgument("nombre") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val nombre = backStackEntry.arguments?.getString("nombre")
                val id = backStackEntry.arguments?.getString("id")
                if (nombre != null) {
                    if (id != null) {
                        mostrarBarra.value = false
                        ScreenPerfil(navController, nombre, id)
                    }
                }
            }
            composable(AppScreens.ItemFavoritos.route) {
                mostrarBarra.value = true
                ItemFavoritos(navController)
            }
        }
    }
}
