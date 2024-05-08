package com.diego.tupro.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.diego.tupro.screenPrincipal.ItemBuscar
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

    NavHost(navController, AppScreens.ItemInicio.route){
        composable(AppScreens.ItemInicio.route){
            ItemInicio(navController)
        }
        composable(AppScreens.ItemBuscar.route){
            ItemBuscar(navController)
        }
        composable(AppScreens.ItemPerfil.route){
            ItemPerfil(navController)
        }
        composable(AppScreens.ScreenPartido.route) {
            ScreenPartido(navController)
        }
        composable(AppScreens.ScreenSesion.route) {
            ScreenSesion(navController)
        }
        composable(AppScreens.ScreenRegistro.route) {
            ScreenRegistro(navController)
        }
        composable(AppScreens.ScreenVerificacion.route) {
            ScreenVerificacion(navController)
        }
        composable(AppScreens.ScreenCrearEquipo.route) {
            ScreenCrearEquipo(navController)
        }
        composable(AppScreens.ScreenCrearCompeticion.route) {
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
                            ScreenEquipo(navController, codigo, creador, equipo, id)
                        }
                    }
                }
            }
        }
        composable(AppScreens.ScreenCompeticion.route) {
            ScreenCompeticion(navController)
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
                    ScreenPerfil(navController, nombre, id)
                }
            }
        }
    }
}
