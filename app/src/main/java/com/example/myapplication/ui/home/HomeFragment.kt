package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
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

    // ViewModel con Factory y Repository
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupBackButtonBehavior()
        setupRecyclerView()
        setupFAB()
        observeViewModel()
    }

    // 🔙 Evitar volver al login con el botón atrás
    private fun setupBackButtonBehavior() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity() // Cierra completamente la app
        }
    }

    // ⚙️ Toolbar con botón de cerrar sesión
    private fun setupToolbar() {
        binding.toolbarHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    // 🔒 Cerrar sesión y borrar SharedPreferences
                    val sharedPref = requireActivity().getSharedPreferences(
                        "UserSession",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    val editor = sharedPref.edit()
                    editor.clear() // Borra la sesión guardada
                    editor.apply()

                    Toast.makeText(requireContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show()

                    // 🔁 Volver al Login y limpiar historial
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    // 🧾 Configuración del RecyclerView
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    // ➕ Botón flotante para agregar producto
    private fun setupFAB() {
        binding.fabAgregarProducto.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_agregarProductoFragment)
            }
        }
    }

    // 👀 Observadores del ViewModel
    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateList(products)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressCircular.isVisible = isLoading
            binding.rvProductos.isVisible = !isLoading
            binding.fabAgregarProducto.isVisible = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


