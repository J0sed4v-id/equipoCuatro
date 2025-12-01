package com.example.myapplication.ui.agregarproducto

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.domain.Product
import com.example.myapplication.ui.widget.InventoryAppWidget
import kotlinx.coroutines.launch

class AgregarProductoViewModel(private val repository: ProductRepository) : ViewModel() {

    // LiveData de los campos del formulario
    val codigo = MutableLiveData<String>("")
    val nombre = MutableLiveData<String>("")
    val precio = MutableLiveData<String>("")
    val cantidad = MutableLiveData<String>("")

    // Validación del formulario
    val isFormValid = MediatorLiveData<Boolean>().apply {
        addSource(codigo) { validateForm() }
        addSource(nombre) { validateForm() }
        addSource(precio) { validateForm() }
        addSource(cantidad) { validateForm() }
    }

    // Estado del guardado
    private val _saveSuccessful = MutableLiveData<Boolean?>()
    val saveSuccessful: LiveData<Boolean?> = _saveSuccessful


    private fun String?.isNumericAndNotEmpty(): Boolean {
        return !this.isNullOrEmpty() && this.toDoubleOrNull() != null
    }

    private fun validateForm() {
        val isValid = !codigo.value.isNullOrEmpty() && codigo.value?.length == 4 &&
                !nombre.value.isNullOrEmpty() &&
                !precio.value.isNullOrEmpty() && precio.value.isNumericAndNotEmpty() &&
                !cantidad.value.isNullOrEmpty() && cantidad.value.isNumericAndNotEmpty() &&
                cantidad.value?.length!! <= 4

        isFormValid.value = isValid
    }

    // 3. MODIFICAR LA FIRMA DE LA FUNCIÓN
    fun saveProduct(context: Context) {
        if (isFormValid.value == true) {
            val newProduct = Product(
                codigo = codigo.value.orEmpty(),
                nombre = nombre.value.orEmpty(),
                precio = precio.value?.toDoubleOrNull() ?: 0.0,
                cantidad = cantidad.value?.toIntOrNull() ?: 0
            )

            viewModelScope.launch {
                try {
                    repository.insert(newProduct)
                    _saveSuccessful.value = true

                    // 4. AÑADIR LA LLAMADA PARA ACTUALIZAR EL WIDGET
                    InventoryAppWidget.triggerUpdate(context.applicationContext)

                } catch (e: Exception) {
                    _saveSuccessful.value = false
                }
            }
        }
    }

    //Limpia el estado del guardado para evitar que se repita
    fun resetSaveStatus() {
        _saveSuccessful.value = null
    }
}
