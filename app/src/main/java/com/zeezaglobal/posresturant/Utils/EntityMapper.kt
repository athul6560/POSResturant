package com.zeezaglobal.posresturant.Utils

import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Retrofit.GroupResponse

object EntityMapper {

    fun mapGroupResponseToEntities(groupResponse: List<GroupResponse>): Pair<List<Group>, List<Item>> {
        val groups = mutableListOf<Group>()
        val items = mutableListOf<Item>()

        for (groupDto in groupResponse) {
            // Convert group
            val group = Group(
                groupId = groupDto.groupId,
                groupName = groupDto.groupName
            )
            groups.add(group)

            // Convert all items inside this group
            for (itemDto in groupDto.items) {
                val item = Item(
                    itemId = itemDto.itemId,
                    groupId = groupDto.groupId,
                    itemName = itemDto.itemName,
                    itemDescription = itemDto.itemDescription,
                    itemPrice = itemDto.itemPrice
                )
                items.add(item)
            }
        }

        return Pair(groups, items)
    }
}