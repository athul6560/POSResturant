package com.zeezaglobal.posresturant.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zeezaglobal.posresturant.Entities.Group


@Dao
interface GroupDao {

    @Insert
    suspend fun insertGroup(group: Group)

    @Query("SELECT * FROM group_table")
    suspend fun getAllGroups(): List<Group>

    @Query("DELETE FROM group_table WHERE groupId = :groupId")
    suspend fun deleteGroup(groupId: Int)
}