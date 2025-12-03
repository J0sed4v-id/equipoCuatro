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
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.ViewModelFactory
import com.example.myapplication.ui.login.LoginActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth // Importante para cerrar sesión real

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter

    // ViewModel con Factory y Repository
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory(requireActivity().application as MyApplication)
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

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbarHome)
        setupToolbar()
        setupBackButtonBehavior()
        setupRecyclerView()
        setupFAB()
        observeViewModel()
    }

    // Evitar volver al login con el botón atrás
    private fun setupBackButtonBehavior() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity() // Cierra completamente la app
        }
    }

    // Toolbar con botón de cerrar sesión
    private fun setupToolbar() {
        // 1. Configuración visual básica
        binding.toolbarHome.subtitle = null

        // 2. Limpiamos por si acaso había basura de otra pantalla...
        binding.toolbarHome.menu.clear()

        // 3. ...¡Y AQUÍ CARGAMOS EL MENÚ CORRECTO! (Esta es la línea que faltaba)
        binding.toolbarHome.inflateMenu(R.menu.menu_home)

        // 4. Ahora sí, escuchamos el clic
        binding.toolbarHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    // 1. Cerrar sesión en FIREBASE
                    FirebaseAuth.getInstance().signOut()

                    // 2. Borrar SharedPreferences
                    val sharedPref = requireActivity().getSharedPreferences(
                        "UserSession",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    with(sharedPref.edit()) {
                        clear()
                        apply()
                    }

                    Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()

                    // 3. Volver al Login
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    // Configuración del RecyclerView
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product.codigo.toInt())
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
