package com.zeezaglobal.posresturant.Repository

import com.zeezaglobal.posresturant.Dao.GroupDao
import com.zeezaglobal.posresturant.Entities.Group
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupRepository(private val groupDao: GroupDao) {

    // Insert a new group
    suspend fun insertGroup(group: Group) = withContext(Dispatchers.IO) {
        groupDao.insertGroup(group)
    }

    // Get all groups
    suspend fun getAllGroups(): List<Group> = withContext(Dispatchers.IO) {
        groupDao.getAllGroups()
    }

    // Delete a specific group by ID
    suspend fun deleteGroup(groupId: Int) = withContext(Dispatchers.IO) {
        groupDao.deleteGroup(groupId)
    }

    // Delete all groups (cascades to items)
    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        groupDao.deleteAllGroups()
    }

    // Insert group and return its generated ID
    suspend fun insertAndGetId(group: Group): Long = withContext(Dispatchers.IO) {
        groupDao.insertGroupAndGetId(group)
    }
}