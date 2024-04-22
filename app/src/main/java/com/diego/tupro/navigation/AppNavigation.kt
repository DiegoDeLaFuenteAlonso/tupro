package com.diego.tupro.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.screenPrincipal.BarraInferior
import com.diego.tupro.screenPrincipal.ItemBuscar
import com.diego.tupro.screenPrincipal.ItemInicio
import com.diego.tupro.screenPrincipal.ItemPerfil
import com.diego.tupro.screenSecundaria.ScreenPartido

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    Scaffold (
        bottomBar = { BarraInferior(navController, 0) }

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
            composable(AppScreens.ScreenPartido.route){
                ScreenPartido(navController)
            }
        }
    }
}