package com.example.myapplication.domain

data class Product(
    val codigo: String,
    val nombre: String,
    val precio: Double,
    val cantidad: Int
) {

    fun formattedPrice(): String {
        // Formato: $00.000,00
        val priceString = String.format("%,.2f", precio)
            .replace('.', '_')
            .replace(',', '.')
            .replace('_', ',')

        return "$$priceString"
    }
}