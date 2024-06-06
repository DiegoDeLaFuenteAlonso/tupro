package com.diego.tupro

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.diego.tupro.screenPrincipal.ItemBusqueda

class Constantes {
    companion object {
        val redondeoBoton = 14.dp
        var usuario = ""
        var correo = ""
        var sesionIniciada = false
        // item buscar
        var guardadoTextoBuscar = ""
        val guardadoResultBusqueda = mutableListOf<ItemBusqueda>()
        // item favoritos
        var favSelectedTabIndex = 0

        fun reiniciarNavegacion(navController: NavController) {
            navController.navigate("item_inicio") {
                popUpTo("item_inicio") {
                    inclusive = true
                }
            }
        }
        @Composable
        fun CargarDatosSesion(){
            val context = LocalContext.current
            val sharedPref = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)

            usuario = sharedPref.getString("username", "").toString()
            correo = sharedPref.getString("email", "").toString()
            sesionIniciada = sharedPref.getString("sesionIniciada", "").toBoolean()
        }
    }
}
