package com.diego.tupro.screenSecundaria

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.twotone.SportsSoccer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.diego.prueba.ui.theme.TuproTheme
import com.diego.tupro.Constantes
import com.diego.tupro.screenPrincipal.DibujarColumnaItems
import com.diego.tupro.screenPrincipal.ItemBusqueda
import com.diego.tupro.screenPrincipal.TabItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenCompeticion(navController: NavController) {
    Scaffold(
        topBar = {
            Column{
                TopAppBar(
                    title = {
                        Text(text = "Competicion", color = colorScheme.primary, fontSize = 26.sp)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { /*TODO añadir a favoritos*/ }
                        ) {
                            Icon(
                                Icons.Outlined.FavoriteBorder,
                                contentDescription = "Añadir a favoritos"
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    ) {innerPadding ->
        BodyContentCompeticion(innerPadding, navController)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BodyContentCompeticion(innerPadding: PaddingValues, navController: NavController) {
    val equipo = ItemBusqueda("LB", "La Bañeza", "1", "Diego", "Equipo")
    val competiciones = remember { mutableStateListOf<ItemBusqueda>(ItemBusqueda("LB", "La Bañeza", "1", "Diego", "Equipo"), ItemBusqueda("LB", "La Bañeza", "1", "Diego", "Equipo")) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Row(
            modifier = Modifier
                //.weight(0.2f)
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(Constantes.redondeoBoton))
                        .background(colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        equipo.codigo.uppercase(Locale.ROOT),
                        color = colorScheme.onSecondaryContainer,
                        fontSize = 30.sp
                    )
                }
                Text(text = equipo.nombre + " #" + equipo.idDocumento)
                Text(text = equipo.creador)
            }
        }
        HorizontalDivider()

        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabItems = listOf(
            TabItem("Partidos", Icons.TwoTone.SportsSoccer, Icons.Outlined.SportsSoccer),
            TabItem("Clasificación", Icons.Filled.Equalizer, Icons.Outlined.Equalizer)
        )
        val pagerState = rememberPagerState {
            tabItems.size
        }
        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress) {
                selectedTabIndex = pagerState.currentPage
            }
        }

        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = (selectedTabIndex == index),
                    onClick = {
                        selectedTabIndex = index
                    },
                    modifier = Modifier
                        .padding(
                            top = 6.dp,
                            bottom = 6.dp
                        )
                ) {
                    Icon(
                        imageVector = if (index == selectedTabIndex) item.selecIcon else item.unselecIcon,
                        contentDescription = item.titulo
                    )
                    Text(text = item.titulo)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { index ->
            if (index == 0) {
                val lista = listOf(
                    ItemClasificacion("Equipo 1", 10, 5, 3, 1, 1, 8, 4),
                    ItemClasificacion("Equipo 2", 9, 5, 3, 0, 2, 7, 5),
                    ItemClasificacion("Equipo 3", 8, 5, 2, 2, 1, 6, 4),
                )
                //Text(text = "hola")

                Column(
                    Modifier.padding(10.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(text = "Equipo")
                        Text(text = "Puntos")
                        Text(text = "PJ")
                        Text(text = "PG")
                        Text(text = "PE")
                        Text(text = "PP")
                        Text(text = "GF")
                        Text(text = "GC")
                    }
                    TablaClasificacion(lista)
                }

            } else if (index == 1) {

            }
        }
    }
}

@Composable
fun TablaClasificacion(lista: List<ItemClasificacion>) {
    LazyColumn {
        items(lista) { item ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(colorScheme.secondaryContainer),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = item.nombre, color = colorScheme.onSecondaryContainer)
                Text(text = item.puntos.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.partidosJugados.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.partidosGanados.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.partidosEmpatados.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.partidosPerdidos.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.golesFavor.toString(), color = colorScheme.onSecondaryContainer)
                Text(text = item.golesContra.toString(), color = colorScheme.onSecondaryContainer)
            }
        }
    }
}

data class ItemClasificacion(
    val nombre: String,
    val puntos: Int,
    val partidosJugados: Int,
    val partidosGanados: Int,
    val partidosEmpatados: Int,
    val partidosPerdidos: Int,
    val golesFavor: Int,
    val golesContra: Int
)


@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewCompeticion1() {
    TuproTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenCompeticion(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkCompeticion1() {
    TuproTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenCompeticion(navController)
    }
}