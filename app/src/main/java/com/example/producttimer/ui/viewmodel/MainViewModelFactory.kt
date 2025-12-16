package com.example.producttimer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producttimer.data.local.UserPreferencesDataStore
import com.example.producttimer.data.remote.repository.ApiRepository

class MainViewModelFactory(
    private val repositorioProducto: ApiRepository,
    private val prefs: UserPreferencesDataStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repositorioProducto, prefs) as T
        }
        throw IllegalArgumentException("clase de ViewModel desconocida")
    }
}