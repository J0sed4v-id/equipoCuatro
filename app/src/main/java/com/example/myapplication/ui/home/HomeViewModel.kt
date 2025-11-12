package com.example.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.domain.Product
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class HomeViewModel(repository: ProductRepository) : ViewModel() {

    // Representa el estado de carga
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    // Representa la lista de productos
    val products: LiveData<List<Product>> = repository.allProducts
        // Antes de emitir datos, muestra el progress bar
        .onStart { _isLoading.value = true }
        // Cuando se emiten los datos, oculta el progress bar
        .map { list ->
            _isLoading.value = false
            list
        }
        // Manejo de errores
        .catch {
            _isLoading.value = false
        }
        .asLiveData()
}