package com.zeezaglobal.posresturant.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Unique ID for each sale
    val billNumber: Long,
    val tokenNumber: Long,
    val totalAmount: Double,
    val dateTime: String
)