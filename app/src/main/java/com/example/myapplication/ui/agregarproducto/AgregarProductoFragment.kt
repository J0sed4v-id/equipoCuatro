package com.example.myapplication.ui.agregarproducto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.databinding.FragmentAgregarProductoBinding
import com.example.myapplication.ui.ViewModelFactory
import androidx.core.content.ContextCompat


class AgregarProductoFragment : Fragment() {

    private var _binding: FragmentAgregarProductoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AgregarProductoViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repository = ProductRepository(db.productDao())
        ViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgregarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupInputFields()
        setupSaveButton()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbarAgregarProducto.title = "Agregar producto"
        binding.toolbarAgregarProducto.setTitleTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        binding.toolbarAgregarProducto.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupInputFields() {
        binding.etCodigoProducto.doAfterTextChanged { viewModel.codigo.value = it.toString() }
        binding.etNombreProducto.doAfterTextChanged { viewModel.nombre.value = it.toString() }
        binding.etPrecioProducto.doAfterTextChanged { viewModel.precio.value = it.toString() }
        binding.etCantidadProducto.doAfterTextChanged { viewModel.cantidad.value = it.toString() }

        binding.etCodigoProducto.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && viewModel.codigo.value?.length != 4 && !viewModel.codigo.value.isNullOrEmpty()) {
                binding.tilCodigoProducto.error = "Debe tener 4 dígitos"
            } else {
                binding.tilCodigoProducto.error = null
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnGuardarProducto.setOnClickListener {
            viewModel.saveProduct()
        }
    }

    private fun observeViewModel() {
        viewModel.isFormValid.observe(viewLifecycleOwner) { isValid ->
            binding.btnGuardarProducto.isEnabled = isValid
        }

        viewModel.saveSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            when (isSuccessful) {
                true -> {
                    Toast.makeText(requireContext(), "Producto guardado con éxito", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_agregarProductoFragment_to_homeFragment)
                    viewModel.resetSaveStatus()
                }

                false -> {
                    if (viewModel.isFormValid.value == true) {
                        Toast.makeText(requireContext(), "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetSaveStatus()
                }

                null -> {
                    // No hacer nada
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
