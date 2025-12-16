package com.example.producttimer.data.model

data class ProductoRequest(
    val nombre: String,
    val descripcion: String?,
    val fechaVencimiento: String,
    val imagen: String?,
    val usuarioId: Int,
    val firebaseUid: String
)