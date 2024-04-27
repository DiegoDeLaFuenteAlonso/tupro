package com.diego.tupro.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.screenPrincipal.ItemBuscar
import com.diego.tupro.screenPrincipal.ItemInicio
import com.diego.tupro.screenPrincipal.ItemPerfil
import com.diego.tupro.screenSecundaria.ScreenPartido
import com.diego.tupro.screenSecundaria.ScreenRegistro
import com.diego.tupro.screenSecundaria.ScreenSesion
import com.diego.tupro.screenSecundaria.ScreenVerificacion

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    /*
    Scaffold (
        // bottomBar = { BarraInferior(navController, 0) }

    ){
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
        }
    }
    */
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
    }
}
