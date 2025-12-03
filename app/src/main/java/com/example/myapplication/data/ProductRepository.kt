package com.example.myapplication.data

import com.example.myapplication.domain.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val productCollection = db.collection("products")

    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val listener = productCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val productList = snapshot?.documents?.mapNotNull {
                it.toObject(Product::class.java)?.copy(id = it.id)
            } ?: emptyList()
            trySend(productList)
        }
        awaitClose { listener.remove() }
    }

    suspend fun insert(product: Product) {
        productCollection.add(product).await()
    }

    fun getProductById(id: String): Flow<Product> = callbackFlow {        val listener = productCollection.document(id).addSnapshotListener { snapshot, e ->
        if (e != null) {
            close(e)
            return@addSnapshotListener
        }
        val product = snapshot?.toObject(Product::class.java)?.copy(id = snapshot.id)
        if (product != null) {
            trySend(product)
        }
    }
        awaitClose { listener.remove() }
    }

    suspend fun update(product: Product) {
        productCollection.document(product.id).set(product).await()
    }

    suspend fun deleteById(id: String) {
        productCollection.document(id).delete().await()
    }
}