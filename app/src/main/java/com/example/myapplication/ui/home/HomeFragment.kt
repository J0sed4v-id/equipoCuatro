package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import android.content.Intent
import com.example.myapplication.AgregarProductoActivity


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root          //
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAgregarProducto.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_agregarProductoFragment)
        }
        configurarToolbar()
        configurarBackButton()
        configurarLista()
    }


    private fun configurarToolbar() {
        binding.toolbarHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {  //
                    Toast.makeText(requireContext(), "Cerrar sesión", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun configurarBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }
    }

    private fun configurarLista() {

        binding.fabAgregarProducto.setOnClickListener {
            val intent = Intent(requireContext(), AgregarProductoActivity::class.java)
            startActivity(intent)

        }

        binding.progressCircular.visibility = View.VISIBLE
        binding.rvProductos.visibility = View.GONE

        binding.rvProductos.postDelayed({
            val productos = listOf(
                Product("001", "Cuaderno"),
                Product("002", "Bolígrafo"),
                Product("003", "Cartulina"),
                Product("004", "Regla")
            )

            binding.rvProductos.layoutManager = LinearLayoutManager(requireContext())

            binding.rvProductos.adapter = ProductAdapter(productos)


            binding.progressCircular.visibility = View.GONE
            binding.rvProductos.visibility = View.VISIBLE
        }, 2000) // 2000 milisegundos = 2 segundos
    }
}