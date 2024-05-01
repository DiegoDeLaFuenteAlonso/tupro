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
import com.diego.tupro.screenSecundaria.ScreenCrearCompeticion
import com.diego.tupro.screenSecundaria.ScreenCrearEquipo
import com.diego.tupro.screenSecundaria.ScreenPartido
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
            route = "screen_busqueda_equipos/{comp}/{codigo}",
            arguments = listOf(
                navArgument("comp") { type = NavType.StringType },
                navArgument("codigo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val comp = backStackEntry.arguments?.getString("comp")
            val codigo = backStackEntry.arguments?.getString("codigo")
            ScreenBusquedaEquipos(navController, comp, codigo)
        }

    }
}
