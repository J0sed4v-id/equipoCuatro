package com.example.myapplication.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.domain.Product
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductDetailViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    fun getProductById(id: String) {
        viewModelScope.launch {
            repository.getProductById(id).collect {
                _product.value = it
            }
        }
    }

    private val _deleteSuccessful = MutableLiveData<Boolean?>()
    val deleteSuccessful: LiveData<Boolean?> = _deleteSuccessful

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteById(id)
                _deleteSuccessful.value = true
            } catch (e: Exception) {
                _deleteSuccessful.value = false
            }
        }
    }

    fun resetDeleteStatus() {
        _deleteSuccessful.value = null
    }
}
