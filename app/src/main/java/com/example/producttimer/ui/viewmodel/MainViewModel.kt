package com.example.producttimer.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.producttimer.data.local.UserPreferencesDataStore
import com.example.producttimer.data.model.Producto
import com.example.producttimer.data.remote.repository.ApiRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val repositorioProducto: ApiRepository,
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    private val _listaProductos = MutableStateFlow<List<Producto>>(emptyList())
    val listaProductos: StateFlow<List<Producto>> = _listaProductos

    val currentNeonUserIdFlow = prefs.currentNeonUserIdFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        fetchProductos()
    }

    fun fetchProductos() {
        viewModelScope.launch {
            try {
                _listaProductos.value = repositorioProducto.getProductos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun obtenerProducto(id: Int): Producto? {
        return try {
            repositorioProducto.getProducto(id)
        } catch (e: Exception) {
            null
        }
    }

    fun guardarNuevoProducto(
        nombre: String,
        descripcion: String?,
        fecha: String,
        imagenLocalUri: String?,
        usuarioId: Int,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("No hay usuario autenticado")

                repositorioProducto.crearProducto(
                    nombre = nombre,
                    descripcion = descripcion,
                    fechaVencimiento = convertirFecha(fecha),
                    imagenLocalUri = imagenLocalUri,
                    usuarioId = usuarioId,
                    firebaseUid = firebaseUid
                )

                onComplete(true, null)
                fetchProductos()

            } catch (e: Exception) {
                onComplete(false, e.message ?: "Error desconocido al guardar")
            }
        }
    }


    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            try {
                val producto = repositorioProducto.getProducto(id)

                producto.imagen?.let { uri ->
                    val file = File(Uri.parse(uri).path ?: "")
                    if (file.exists()) file.delete()
                }

                repositorioProducto.eliminarProducto(id)
                fetchProductos()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun convertirFecha(fecha: String): String {
        val partes = fecha.split("/")
        if (partes.size != 3) return fecha
        return "${partes[2]}-${partes[1]}-${partes[0]}"
    }
}