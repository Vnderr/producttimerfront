package com.example.producttimer.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object VerificadorConexion {

    fun hayInternet(context: Context): Boolean {
        val gestor = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val redActiva = gestor.activeNetwork ?: return false
        val capacidades = gestor.getNetworkCapabilities(redActiva) ?: return false

        return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}