package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarToolbar()
        configurarBackButton()
        configurarLista()
    }

    private fun configurarToolbar() {
        binding.toolbarHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
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
        val productos = listOf(
            Product("001", "Cuaderno"),
            Product("002", "Bolígrafo"),
            Product("003", "Cartulina"),
            Product("004", "Regla")
        )

        binding.rvProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductos.adapter = ProductAdapter(productos)
    }
}