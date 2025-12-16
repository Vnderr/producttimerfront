package com.example.producttimer.data.remote

import com.example.producttimer.data.model.Producto
import com.example.producttimer.data.model.ProductoRequest
import com.example.producttimer.data.model.Usuario
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/productos")
    suspend fun getProductos(): List<Producto>

    @GET("api/productos/{id}")
    suspend fun getProducto(@Path("id") id: Int): Producto

    /*@POST("api/productos")
    suspend fun getProductosByUsuario(@Path("usuarioId") usuarioId: Int): List<Producto>*/

    @POST("api/productos")
    suspend fun createProducto(@Body producto: ProductoRequest): Producto
    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("api/imagenes/upload")
    suspend fun uploadImagen(@Part file: MultipartBody.Part): Response<ResponseBody>

    @GET("api/usuarios/usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @GET("api/usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Usuario

    @POST("api/usuarios")
    suspend fun createUser(@Body usuario: Usuario): Usuario

    /*@DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Int): Response<Unit>*/

    /*@POST("usuarios/login")
    suspend fun login(@Body loginRequest: Map<String, String>): Usuario*/
}