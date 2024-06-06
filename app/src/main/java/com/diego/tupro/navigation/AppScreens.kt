package com.diego.tupro.navigation

sealed class AppScreens(val route: String) {
    object ItemInicio: AppScreens("item_inicio")
    object ItemBuscar: AppScreens("item_buscar")
    object ItemPerfil: AppScreens("item_perfil")
    object ScreenPartido: AppScreens("screen_partido")
    object ScreenSesion: AppScreens("screen_sesion")
    object ScreenRegistro: AppScreens("screen_registro")
    object ScreenVerificacion: AppScreens("screen_verificacion")
    object ScreenCrearEquipo: AppScreens("screen_crear_equipo")
    object ScreenCrearCompeticion: AppScreens("screen_crear_competicion")
    object ScreenBusquedaEquipos: AppScreens("screen_busqueda_equipos")
    object ScreenEquipo: AppScreens("screen_equipo")
    object ScreenCompeticion: AppScreens("screen_competicion")
    object ScreenPerfil: AppScreens("screen_perfil")
    object ItemFavoritos: AppScreens("item_favoritos")
    object ScreenCrearPartido: AppScreens("screen_crear_partido")
    object ScreenSeleccionarFecha: AppScreens("screen_seleccionar_fecha")
    object ScreenEvento: AppScreens("screen_evento")
    object ScreenNarracion: AppScreens("screen_narracion")
    object ScreenEditarMarcador: AppScreens("screen_editar_marcador")
}