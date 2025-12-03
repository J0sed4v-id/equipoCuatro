package com.example.myapplication.domain

data class Product(
    val id: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0
) {
    fun formattedPrice(): String {
        val priceString = String.format("%,.2f", precio)
            .replace('.', '_')
            .replace(',', '.')
            .replace('_', ',')

        return "$$priceString"
    }
}