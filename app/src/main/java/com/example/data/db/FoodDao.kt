package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_items WHERE isConsumed = 0 AND isWasted = 0 ORDER BY expiryDateMillis ASC")
    fun getActiveItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE isConsumed = 1 ORDER BY insertedDateMillis DESC")
    fun getConsumedItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE isWasted = 1 ORDER BY insertedDateMillis DESC")
    fun getWastedItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items ORDER BY expiryDateMillis ASC")
    fun getAllItems(): Flow<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<FoodItem>): List<Long>

    @Update
    suspend fun updateItem(item: FoodItem)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteItemById(id: Int)

    @Query("DELETE FROM food_items")
    suspend fun clearAll()
}
