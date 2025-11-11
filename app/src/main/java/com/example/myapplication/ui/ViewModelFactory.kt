package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.ui.agregarproducto.AgregarProductoViewModel
import com.example.myapplication.ui.home.HomeViewModel

/**
 * Fábrica de ViewModels para inyectar dependencias (como el Repository).
 */
class ViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AgregarProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarProductoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}