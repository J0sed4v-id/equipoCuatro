package com.example.myapplication.ui.editproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.domain.Product
import kotlinx.coroutines.launch

class EditProductViewModel(private val repository: ProductRepository) : ViewModel() {

    lateinit var product: LiveData<Product>

    fun getProductById(id: String) {
        product = repository.getProductById(id).asLiveData()
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.update(product)
        }
    }
}