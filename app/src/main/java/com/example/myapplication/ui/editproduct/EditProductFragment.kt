package com.example.myapplication.ui.editproduct

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEditProductBinding
import com.example.myapplication.domain.Product
import com.example.myapplication.ui.ViewModelFactory

class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EditProductViewModel
    private val args: EditProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(EditProductViewModel::class.java)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarEditProduct)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarEditProduct.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnEdit.setOnClickListener {
            val name = binding.etProductName.text.toString()
            val price = binding.etProductPrice.text.toString().toDouble()
            val quantity = binding.etProductQuantity.text.toString().toInt()
            val product = Product(args.productId, name, price, quantity)
            viewModel.updateProduct(product)
            findNavController().navigate(R.id.action_editProductFragment_to_homeFragment)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = binding.etProductName.text.toString().trim()
                val price = binding.etProductPrice.text.toString().trim()
                val quantity = binding.etProductQuantity.text.toString().trim()

                binding.btnEdit.isEnabled = name.isNotEmpty() && price.isNotEmpty() && quantity.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etProductName.addTextChangedListener(textWatcher)
        binding.etProductPrice.addTextChangedListener(textWatcher)
        binding.etProductQuantity.addTextChangedListener(textWatcher)

        viewModel.getProductById(args.productId)

        viewModel.product.observe(viewLifecycleOwner) { product ->
            product?.let {
                binding.tvProductId.text = it.id
                binding.etProductName.setText(it.nombre)
                binding.etProductPrice.setText(it.precio.toString())
                binding.etProductQuantity.setText(it.cantidad.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
