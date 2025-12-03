package com.example.myapplication.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.domain.Product
import kotlinx.coroutines.launch

class ProductDetailViewModel(private val repository: ProductRepository) : ViewModel() {

    lateinit var product: LiveData<Product>

    fun getProductById(id: String) {
        product = repository.getProductById(id).asLiveData()
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}