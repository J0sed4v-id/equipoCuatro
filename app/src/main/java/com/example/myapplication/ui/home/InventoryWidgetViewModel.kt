package com.example.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class InventoryWidgetViewModel : ViewModel() {

    private val _isBalanceVisible = MutableLiveData(false)
    val isBalanceVisible: LiveData<Boolean> = _isBalanceVisible

    private val _inventoryBalance = MutableLiveData<String>()
    val inventoryBalance: LiveData<String> = _inventoryBalance

    private val _hiddenBalance = MutableLiveData("****")
    val hiddenBalance: LiveData<String> = _hiddenBalance

    init {
        // Simular carga del saldo del inventario
        loadInventoryBalance()
    }

    private fun loadInventoryBalance() {
        viewModelScope.launch {
            // Aquí iría la lógica para obtener el saldo real de la base de datos
            // Por ahora usamos un valor fijo
            _inventoryBalance.value = "326.000,00"
        }
    }

    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !(_isBalanceVisible.value ?: false)
    }

    fun getDisplayBalance(): String {
        return if (_isBalanceVisible.value == true) {
            _inventoryBalance.value ?: "0,00"
        } else {
            _hiddenBalance.value ?: "****"
        }
    }
}