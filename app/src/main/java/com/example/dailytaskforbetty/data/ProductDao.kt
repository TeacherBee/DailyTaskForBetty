// filePath: dailytaskforbetty/data/ProductDao.kt
package com.example.dailytaskforbetty.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Insert
import com.example.dailytaskforbetty.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun observeAllProducts(): Flow<List<ProductEntity>>

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    @Insert
    suspend fun insertProducts(products: List<ProductEntity>)
}