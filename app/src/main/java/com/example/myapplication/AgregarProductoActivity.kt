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
            val nombre = binding.etNombreProducto.text.toString()
            val cantidad = binding.etCantidadProducto.text.toString()
            val precio = binding.etPrecioProducto.text.toString()


            // Temporal: solo mostrar los valores ingresados (más adelante guardaremos en BD)

            if (nombre.isNotEmpty() && cantidad.isNotEmpty() && precio.isNotEmpty()) {
                // Mostrar mensaje temporal de éxito
                Toast.makeText(this, "Producto guardado: $nombre", Toast.LENGTH_SHORT).show()
                finish() // Regresa al Home
            } else {
                // Avisar si faltan datos
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}