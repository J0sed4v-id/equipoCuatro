package com.example.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ProductRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class InventoryWidgetViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _isBalanceVisible = MutableLiveData(false)
    val isBalanceVisible: LiveData<Boolean> = _isBalanceVisible

    private val _inventoryBalance = MutableLiveData<String>()
    val inventoryBalance: LiveData<String> = _inventoryBalance

    private val _hiddenBalance = MutableLiveData("**")
    val hiddenBalance: LiveData<String> = _hiddenBalance

    init {
        loadInventoryBalance()
    }

    private fun loadInventoryBalance() {
        viewModelScope.launch {
            repository.getAllProducts().collect { products ->
                val totalBalance = products.sumOf { it.precio * it.cantidad }
                _inventoryBalance.value = formatBalance(totalBalance)
            }
        }
    }

    private fun formatBalance(balance: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        return format.format(balance).replace("COP", "$")
    }

    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !(_isBalanceVisible.value ?: false)
    }

    fun getDisplayBalance(): String {
        return if (_isBalanceVisible.value == true) {
            _inventoryBalance.value ?: "$ 0,00"
        } else {
            _hiddenBalance.value ?: "**"
        }
    }
}