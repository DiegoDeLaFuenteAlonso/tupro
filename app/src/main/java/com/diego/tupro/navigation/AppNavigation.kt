package com.diego.tupro.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diego.tupro.BarraInferior
import com.diego.tupro.IniciarInterfaz
import com.diego.tupro.ItemBuscar
import com.diego.tupro.ItemInicio
import com.diego.tupro.ItemPerfil
import com.diego.tupro.BarraInferior
import com.diego.tupro.ItemInicio
import com.diego.tupro.ItemPerfil

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
        }
    }
}