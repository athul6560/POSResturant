package com.zeezaglobal.posresturant.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_table")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val groupId: Int = 0, // Auto-generated ID for each group
    val groupName: String // Name of the group
)