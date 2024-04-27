package com.diego.tupro.navigation

sealed class AppScreens(val route: String) {
    object ItemInicio: AppScreens("item_inicio")
    object ItemBuscar: AppScreens("item_buscar")
    object ItemPerfil: AppScreens("item_perfil")
    object ScreenPartido: AppScreens("screen_partido")
    object ScreenSesion: AppScreens("screen_sesion")
    object ScreenRegistro: AppScreens("screen_registro")
    object ScreenVerificacion: AppScreens("screen_verificacion")
}