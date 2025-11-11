package com.example.myapplication.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemProductBinding
import com.example.myapplication.domain.Product

class ProductAdapter(
    private var productos: List<Product> = emptyList()
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun updateList(newList: List<Product>) {
        productos = newList
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(p: Product) {
            // Nombre del producto
            binding.tvNombre.text = p.nombre
            // ID del producto
            binding.tvId.text = "ID: ${p.codigo}"
            // Precio con formato
            binding.tvPrecio.text = p.formattedPrice()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount(): Int = productos.size
}
