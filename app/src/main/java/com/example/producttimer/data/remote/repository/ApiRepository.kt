package com.example.producttimer.data.remote.repository

import com.example.producttimer.data.model.Producto
import com.example.producttimer.data.model.ProductoRequest
import com.example.producttimer.data.remote.ApiService
import retrofit2.Response

class ApiRepository(private val api: ApiService) {

    suspend fun getProductos(): List<Producto> = api.getProductos()

    suspend fun getProducto(id: Int): Producto = api.getProducto(id)

    suspend fun crearProducto(
        nombre: String,
        descripcion: String?,
        fechaVencimiento: String,
        imagenLocalUri: String?,
        usuarioId: Int,
        firebaseUid: String
    ): Producto {

        val request = ProductoRequest(
            nombre = nombre,
            descripcion = descripcion,
            fechaVencimiento = fechaVencimiento,
            imagen = imagenLocalUri,
            usuarioId = usuarioId,
            firebaseUid = firebaseUid
        )

        return api.createProducto(request)
    }

    suspend fun eliminarProducto(id: Int): Response<Unit> = api.eliminarProducto(id)
}