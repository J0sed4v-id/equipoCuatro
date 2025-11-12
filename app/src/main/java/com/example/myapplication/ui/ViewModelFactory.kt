package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MyApplication
import com.example.myapplication.ui.agregarproducto.AgregarProductoViewModel
import com.example.myapplication.ui.editproduct.EditProductViewModel
import com.example.myapplication.ui.home.HomeViewModel
import com.example.myapplication.ui.home.InventoryWidgetViewModel
import com.example.myapplication.ui.productdetail.ProductDetailViewModel

class ViewModelFactory(private val application: MyApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application.repository) as T
        }
        if (modelClass.isAssignableFrom(AgregarProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarProductoViewModel(application.repository) as T
        }
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(application.repository) as T
        }
        if (modelClass.isAssignableFrom(EditProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProductViewModel(application.repository) as T
        }
        if (modelClass.isAssignableFrom(InventoryWidgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryWidgetViewModel(application.repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}