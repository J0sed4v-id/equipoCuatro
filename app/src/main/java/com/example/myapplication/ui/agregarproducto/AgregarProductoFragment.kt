package com.example.myapplication.ui.agregarproducto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentAgregarProductoBinding

class AgregarProductoFragment : Fragment() {

    private var _binding: FragmentAgregarProductoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgregarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón atrás
        binding.toolbarAgregarProducto.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Acción del botón que guarda el producto
        binding.btnGuardarProducto.setOnClickListener {
            val codigo = binding.etCodigoProducto.text.toString()
            val nombre = binding.etNombreProducto.text.toString()
            val cantidad = binding.etCantidadProducto.text.toString()
            val precio = binding.etPrecioProducto.text.toString()

            var valido = true

            if (codigo.isEmpty()) {
                binding.etCodigoProducto.error = "Ingresa un código"
                valido = false
            } else binding.etCodigoProducto.error = null

            if (nombre.isEmpty()) {
                binding.etNombreProducto.error = "Ingresa el nombre"
                valido = false
            } else binding.etNombreProducto.error = null

            if (cantidad.isEmpty()) {
                binding.etCantidadProducto.error = "Ingresa la cantidad"
                valido = false
            } else binding.etCantidadProducto.error = null

            if (precio.isEmpty()) {
                binding.etPrecioProducto.error = "Ingresa el precio"
                valido = false
            } else binding.etPrecioProducto.error = null

            if (valido) {
                Toast.makeText(requireContext(), "Producto guardado: $nombre", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Regresa al Home
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}