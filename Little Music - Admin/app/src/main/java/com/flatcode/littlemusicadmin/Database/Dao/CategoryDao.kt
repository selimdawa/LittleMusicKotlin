package com.flatcode.littlemusicadmin.Database.Dao

import androidx.room.*
import com.flatcode.littlemusicadmin.Model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): Category?

    @Delete
    suspend fun deleteCategory(category: Category)
}