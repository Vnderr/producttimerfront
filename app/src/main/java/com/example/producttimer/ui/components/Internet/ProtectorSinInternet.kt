package com.example.producttimer.ui.components.Internet

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.producttimer.utils.VerificadorConexion

@Composable
fun ProtectorSinInternet(
    contenido: @Composable () -> Unit
) {
    val contexto = LocalContext.current
    var conectado by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        conectado = VerificadorConexion.hayInternet(contexto)
    }

    if (conectado) {
        contenido()
    } else {
        PantallaSinInternet()
    }
}