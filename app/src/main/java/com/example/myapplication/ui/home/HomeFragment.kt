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
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.ViewModelFactory
import com.example.myapplication.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavOptions

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter

    // ViewModel con Factory y Repository
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory()
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

    //Evitar volver al login con el botón atrás
    private fun setupBackButtonBehavior() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity() // Cierra completamente la app
        }
    }

    // Toolbar con botón de cerrar sesión
    private fun setupToolbar() {

        binding.toolbarHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    // 1. Cerrar la sesión de Firebase, que es lo más importante.
                    FirebaseAuth.getInstance().signOut()

                    Toast.makeText(requireContext(), "Has cerrado sesión", Toast.LENGTH_SHORT).show()

                    // 2. Crear un Intent para volver a LoginActivity.
                    val intent = Intent(requireContext(), LoginActivity::class.java)

                    // 3. Añadir flags para limpiar el historial de navegación.
                    //    - FLAG_ACTIVITY_NEW_TASK: Inicia la actividad en una nueva tarea.
                    //    - FLAG_ACTIVITY_CLEAR_TASK: Borra todas las actividades de la tarea antes de iniciar la nueva.
                    //    Esto asegura que el usuario no pueda volver al HomeFragment con el botón "Atrás".
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    // 4. Iniciar la actividad y cerrar la actual.
                    startActivity(intent)
                    requireActivity().finish()

                    true
                }

                else -> false
            }
        }
    }

    // Configuración del RecyclerView
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product.id)
            findNavController().navigate(action)
        }
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    // Botón flotante para agregar producto
    private fun setupFAB() {
        binding.fabAgregarProducto.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_agregarProductoFragment)
            }
        }

    }

    // Observadores del ViewModel
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
