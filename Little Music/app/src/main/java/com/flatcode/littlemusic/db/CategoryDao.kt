package com.flatcode.littlemusic.db

import androidx.room.*
import com.flatcode.littlemusic.model.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(categories: List<Category>)

    @Delete
    fun deleteCategory(category: Category)
}