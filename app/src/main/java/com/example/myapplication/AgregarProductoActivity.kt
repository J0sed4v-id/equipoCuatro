package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityAgregarProductoBinding
class AgregarProductoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarProductoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Configuracion de la  toolbar
        setSupportActionBar(binding.toolbarAgregarProducto)
        supportActionBar?.title = "Agregar Producto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Botón atras
        binding.toolbarAgregarProducto.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Acción del botón que guarda el producto
        binding.btnGuardarProducto.setOnClickListener {
            val codigo = binding.etCodigoProducto.text.toString()
            val nombre = binding.etNombreProducto.text.toString()
            val cantidad = binding.etCantidadProducto.text.toString()
            val precio = binding.etPrecioProducto.text.toString()


            //validaciones

            var valido = true

            if (codigo.isEmpty()) {
                binding.etCodigoProducto.error = "Ingresa un código"
                valido = false
            } else {
                binding.etCodigoProducto.error = null
            }

            if (nombre.isEmpty()) {
                binding.etNombreProducto.error = "Ingresa el nombre"
                valido = false
            } else {
                binding.etNombreProducto.error = null
            }

            if (cantidad.isEmpty()) {
                binding.etCantidadProducto.error = "Ingresa la cantidad"
                valido = false
            } else {
                binding.etCantidadProducto.error = null
            }

            if (precio.isEmpty()) {
                binding.etPrecioProducto.error = "Ingresa el precio"
                valido = false
            } else {
                binding.etPrecioProducto.error = null
            }

            // Si todo es válido, mostrar mensaje temporal y volver al Home
            if (valido) {
                Toast.makeText(this, "Producto guardado: $nombre", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}