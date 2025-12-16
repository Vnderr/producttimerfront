package com.example.producttimer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.producttimer.data.remote.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    fun registrarUsuario(nombre: String, correo: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerUser(nombre, correo, password)
            if (result.isSuccess) onComplete(true, null)
            else onComplete(false, result.exceptionOrNull()?.message)
        }
    }

    fun iniciarSesion(correo: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginUser(correo, password)
            if (result.isSuccess) onComplete(true, null)
            else onComplete(false, result.exceptionOrNull()?.message)
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}