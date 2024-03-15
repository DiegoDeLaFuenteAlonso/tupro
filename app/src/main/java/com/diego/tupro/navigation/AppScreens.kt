package com.diego.tupro.navigation

sealed class AppScreens(val route: String) {
    object ItemInicio: AppScreens("item_inicio")
    object ItemBuscar: AppScreens("item_buscar")
    object ItemPerfil: AppScreens("item_perfil")
}