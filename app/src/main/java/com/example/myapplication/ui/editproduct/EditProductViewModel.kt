package com.example.myapplication.ui.editproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.domain.Product
import kotlinx.coroutines.launch

class EditProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    fun getProductById(id: String) {
        viewModelScope.launch {
            repository.getProductById(id).asLiveData().observeForever {
                _product.value = it
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.update(product)
        }
    }
}