package com.example.producttimer.data.remote.repository

import com.example.producttimer.data.local.UserPreferencesDataStore
import com.example.producttimer.data.model.Usuario
import com.example.producttimer.data.remote.ApiService
import com.example.producttimer.data.remote.AuthClient
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val api: ApiService,
    private val authClient: AuthClient,
    private val prefs: UserPreferencesDataStore
) {
    val currentFirebaseUIDFlow: Flow<String?> = prefs.currentFirebaseUIDFlow

    suspend fun registerUser(nombre: String, correo: String, password: String): Result<Int> {
        return try {
            val firebaseUID = authClient.registrarUsuario(nombre, correo, password).getOrThrow()

            val nuevoUsuario = Usuario(
                usuarioId = null,
                nombre = nombre,
                correo = correo,
                contrasena = password
            )
            val usuarioRemoto = api.createUser(nuevoUsuario)

            val neonId = usuarioRemoto.usuarioId
                ?: throw UserRegistrationException("API no devolvio el ID del usuario")

            prefs.saveFirebaseUID(firebaseUID)
            prefs.saveNeonUserId(neonId)
            prefs.saveUserName(nombre)

            Result.success(neonId)
        } catch (e: Exception) {
            Result.failure(UserRegistrationException("Error al registrar usuario: ${e.message}", e))
        }
    }

    suspend fun loginUser(correo: String, password: String): Result<Int> {
        return try {
            val firebaseUID = authClient.iniciarSesion(correo, password).getOrThrow()

            val usuarios = api.getUsuarios()

            println("Usuarios en la base de datos: $usuarios")

            val usuario = usuarios.firstOrNull { it.correo == correo }
                ?: throw UserLoginException("Usuario no encontrado en la API")

            val neonId = usuario.usuarioId
                ?: throw UserLoginException("API no devolvio el ID del usuario")

            prefs.saveFirebaseUID(firebaseUID)
            prefs.saveNeonUserId(neonId)
            prefs.saveUserName(usuario.nombre)

            Result.success(neonId)
        } catch (e: Exception) {
            Result.failure(UserLoginException("Error al iniciar sesion: ${e.message}", e))
        }
    }

    suspend fun logout() {
        authClient.cerrarSesion()
        prefs.clearSession()
    }
}

class UserRegistrationException(message: String, cause: Throwable? = null) : Exception(message, cause)
class UserLoginException(message: String, cause: Throwable? = null) : Exception(message, cause)