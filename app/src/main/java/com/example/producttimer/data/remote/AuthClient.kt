package com.example.producttimer.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
class AuthClient {
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun registrarUsuario(nombre: String, email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("UID no disponible")

            val usuarioData = hashMapOf(
                "uid" to uid,
                "nombre" to nombre,
                "email" to email
            )

            Result.success(uid)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("El correo ya está registrado."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarSesion(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: throw IllegalStateException("UID no disponible"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() = auth.signOut()

    fun obtenerIdUsuarioActual(): String? = auth.currentUser?.uid
}
