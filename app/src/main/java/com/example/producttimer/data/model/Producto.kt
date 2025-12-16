package com.example.producttimer.data.model


data class Producto(
    val producto_id: Int? = null,
    val nombre: String,
    val descripcion: String?,
    val fechaVencimiento: String,
    val imagen: String?,
    val usuario: Usuario? = null
)