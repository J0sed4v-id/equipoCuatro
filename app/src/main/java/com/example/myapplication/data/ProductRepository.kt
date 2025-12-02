package com.example.myapplication.data

import com.example.myapplication.domain.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ProductRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<Product>> = productDao.getAllProducts().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun insert(product: Product) {
        productDao.insertProduct(product.toEntity())
    }

    fun getProductById(id: String): Flow<Product> {
        return productDao.getProductById(id).map { it.toDomain() }
    }

    suspend fun update(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    suspend fun deleteById(id: String) {
        productDao.deleteProductById(id)
    }
}