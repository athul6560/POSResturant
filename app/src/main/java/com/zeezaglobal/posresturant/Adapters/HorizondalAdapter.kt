package com.zeezaglobal.posresturant.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R

class HorizontalAdapter(
    private var groupList: List<Group>,
    private val onItemClick: (Group) -> Unit
) : RecyclerView.Adapter<HorizontalAdapter.GroupViewHolder>() {

    // ViewHolder to hold references to views for each item
    inner class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupText: TextView = view.findViewById(R.id.group_tv)
    }

    // Inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_view, parent, false)
        return GroupViewHolder(view)
    }

    // Bind data to the views in each item
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = groupList[position]

        // Capitalize the first letter of the group's name
        val displayName = currentGroup.groupName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        holder.groupText.text = displayName

        // Set a click listener for the item
        holder.itemView.setOnClickListener {
            onItemClick(currentGroup)
        }
    }

    // Update the group list and notify the adapter
    fun updateGroups(newGroups: List<Group>) {
        groupList = newGroups
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = groupList.size
}