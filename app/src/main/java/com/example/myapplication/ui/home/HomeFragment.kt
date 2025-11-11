package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.ProductRepository
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.ViewModelFactory
import com.example.myapplication.ui.login.LoginActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter

    // Inicialización del ViewModel con Factory y Repository
    private val viewModel: HomeViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repository = ProductRepository(db.productDao())
        ViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // ⭐ FUNCIÓN CORREGIDA: SOLUCIÓN AL ERROR DE DUPLICIDAD
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupBackButtonBehavior()
        setupRecyclerView()
        setupFAB() // Aseguramos que el FAB se configure
        observeViewModel()
    }
    // ⭐ El resto de las funciones auxiliares se mantienen sin cambios,
    // salvo la corrección en observeViewModel para el FAB.

    /**
     * Configura el comportamiento del botón de atrás (HU-3 C4).
     */
    private fun setupBackButtonBehavior() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // No dirige al login, sino que cierra la aplicación y envía al escritorio
            requireActivity().finishAffinity()
        }
    }


    private fun setupToolbar() {
        binding.toolbarHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    // Lógica para cerrar sesión
                    Toast.makeText(requireContext(), "Cerrando sesión...", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }


    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }


    private fun setupFAB() {
        // Aseguramos que la visibilidad inicial sea VISIBLE.
        binding.fabAgregarProducto.visibility = View.VISIBLE

        binding.fabAgregarProducto.setOnClickListener {
            // Navegación al fragmento AgregarProductoFragment
            findNavController().navigate(R.id.action_homeFragment_to_agregarProductoFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateList(products)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.rvProductos.isVisible = !isLoading

            // CORRECCIÓN/AJUSTE: Aseguramos que el FAB solo esté visible cuando no esté cargando
            if (isLoading) {
                binding.fabAgregarProducto.isVisible = false
            } else {
                binding.fabAgregarProducto.isVisible = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//    private fun observeViewModel() {
//        viewModel.products.observe(viewLifecycleOwner) { products ->
//            productAdapter.updateList(products)
//        }
//
//        // Mostrar/Ocultar Progress Bar
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.isVisible = isLoading
//            binding.rvProductos.isVisible = !isLoading
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//}

