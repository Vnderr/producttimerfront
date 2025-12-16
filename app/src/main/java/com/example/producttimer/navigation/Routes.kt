package com.example.producttimer.navigation

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val PRODUCTOS  = "productos"
    const val SUBIRPRODUCTOS = "subirproductos"
    const val DETAIL = "detail/{remoteId}"
    const val LOGIN = "login"
    const val REGISTER = "register"

    fun detailRoute(remoteId: String) = "detail/$remoteId"
}
