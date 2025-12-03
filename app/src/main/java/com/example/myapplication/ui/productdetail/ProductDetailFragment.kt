package com.example.myapplication.ui.productdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProductDetailBinding
import com.example.myapplication.ui.ViewModelFactory

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductDetailViewModel
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(requireActivity().application as MyApplication)
        viewModel = ViewModelProvider(this, factory).get(ProductDetailViewModel::class.java)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarProductDetail)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarProductDetail.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.fabEdit.setOnClickListener {
            val action = ProductDetailFragmentDirections
                .actionProductDetailFragmentToEditProductFragment(args.productId)
            findNavController().navigate(action)
        }

        // Obtener producto por ID
        viewModel.getProductById(args.productId.toString())

        // Observar cambios del producto
        viewModel.product.observe(viewLifecycleOwner) { product ->
            product?.let {
                binding.tvProductName.text = it.nombre
                binding.tvProductPrice.text = "$ ${it.precio}"
                binding.tvProductQuantity.text = it.cantidad.toString()
                binding.tvProductTotal.text = "$ ${it.precio * it.cantidad}"
            }
        }

        // Observar estado de eliminación
        viewModel.deleteSuccessful.observe(viewLifecycleOwner) { success ->
            when (success) {
                true -> {
                    Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_productDetailFragment_to_homeFragment)
                    viewModel.resetDeleteStatus()
                }
                false -> {
                    Toast.makeText(requireContext(), "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                    viewModel.resetDeleteStatus()
                }
                null -> { /* No hacer nada */ }
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro de que desea eliminar este producto?")
            .setPositiveButton("Si") { _, _ ->
                viewModel.deleteProduct(args.productId.toString())
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
