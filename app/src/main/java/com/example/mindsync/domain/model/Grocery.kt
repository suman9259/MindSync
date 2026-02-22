package com.example.mindsync.domain.model

import java.util.UUID

data class GroceryList(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "My Grocery List",
    val items: List<GroceryItem> = emptyList(),
    val isCompleted: Boolean = false,
    val scheduledDate: Long? = null,
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class GroceryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val quantity: Int = 1,
    val unit: String = "",
    val category: GroceryCategory = GroceryCategory.OTHER,
    val isPurchased: Boolean = false,
    val notes: String = "",
    val price: Double? = null
)

enum class GroceryCategory(val displayName: String, val emoji: String) {
    FRUITS("Fruits", "🍎"),
    VEGETABLES("Vegetables", "🥬"),
    DAIRY("Dairy", "🥛"),
    MEAT("Meat & Poultry", "🍖"),
    SEAFOOD("Seafood", "🐟"),
    BAKERY("Bakery", "🍞"),
    BEVERAGES("Beverages", "🥤"),
    SNACKS("Snacks", "🍿"),
    FROZEN("Frozen Foods", "🧊"),
    CANNED("Canned Goods", "🥫"),
    GRAINS("Grains & Pasta", "🍝"),
    SPICES("Spices & Condiments", "🧂"),
    CLEANING("Cleaning", "🧹"),
    PERSONAL_CARE("Personal Care", "🧴"),
    OTHER("Other", "📦")
}
