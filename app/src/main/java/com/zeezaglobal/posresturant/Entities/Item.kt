package com.zeezaglobal.posresturant.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_table",
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["groupId"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE // If a group is deleted, all related items are deleted
    )]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0, // Auto-generated ID for each item
    val groupId: Int, // Foreign key referencing Group
    val itemName: String, // Name of the item
    val itemDescription: String, // Description of the item
    val itemPrice: Double // Price of the item
)