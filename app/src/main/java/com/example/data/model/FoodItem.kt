package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: String,
    val category: String, // "Dairy", "Fruits & Veggies", "Bakery", "Snacks", "Beverages", "Others"
    val expiryDateMillis: Long,
    val storageLocation: String, // "Refrigerated", "Pantry", "Freezer"
    val isConsumed: Boolean = false,
    val isWasted: Boolean = false,
    val insertedDateMillis: Long = System.currentTimeMillis(),
    val estimatedCost: Double = 0.0 // Costs associated with item (in Rupees, for matching screenshot)
)
