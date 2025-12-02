package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.domain.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val codigo: String,         // Código del producto (Primary Key)
    val nombre: String,         // Nombre del artículo
    val precio: Double,         // Precio
    val cantidad: Int           // Cantidad
)

fun ProductEntity.toDomain(): Product {
    return Product(codigo, nombre, precio, cantidad)
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(codigo, nombre, precio, cantidad)
}