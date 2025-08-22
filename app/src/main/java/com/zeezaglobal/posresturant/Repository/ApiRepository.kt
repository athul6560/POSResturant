package com.zeezaglobal.posresturant.Repository

import com.zeezaglobal.posresturant.Dao.GroupDao
import com.zeezaglobal.posresturant.Dao.ItemDao
import com.zeezaglobal.posresturant.Retrofit.GroupResponse
import com.zeezaglobal.posresturant.Retrofit.RetrofitInstance
import com.zeezaglobal.posresturant.Utils.EntityMapper
import java.io.IOException

class ApiRepository(
    private val groupDao: GroupDao,
    private val itemDao: ItemDao
) {
    suspend fun getGroups(): List<GroupResponse>? {
        return try {
            val response = RetrofitInstance.api.getGroups()
            if (response.isSuccessful) {
                response.body()?.let { g ->
                    // Map and save to Room
                    val (groups, items) = EntityMapper.mapGroupResponseToEntities(g)
                    groupDao.deleteAllGroups()
                    itemDao.deleteAllItems()
                    groupDao.insertGroups(groups)
                    itemDao.insertItems(items)
                    g
                }
            } else {
                null // Server returned error
            }
        } catch (e: IOException) {
            // Network error (like Wi-Fi off)
            e.printStackTrace()
            null
        } catch (e: Exception) {
            // Other unexpected errors
            e.printStackTrace()
            null
        }
    }
}