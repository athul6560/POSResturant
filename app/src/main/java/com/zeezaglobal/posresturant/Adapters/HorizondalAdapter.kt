package com.zeezaglobal.posresturant.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R


class HorizontalAdapter(
    private var groupList: List<Group>,
    private val onItemClick: (Group) -> Unit
) : RecyclerView.Adapter<HorizontalAdapter.GroupViewHolder>() {
    private var selectedPosition: Int = -1
    // ViewHolder to hold references to views for each item
    inner class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupText: TextView = view.findViewById(R.id.group_tv)
        val relativeLayout: RelativeLayout = view.findViewById(R.id.relativeLayout)
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
       // Replace with your desired hex code.
        if (position == selectedPosition) {
            holder.relativeLayout.setBackgroundResource(R.drawable.round_background_red) // Highlight drawable
        } else {
            holder.relativeLayout.setBackgroundResource(R.drawable.round_background_grey) // Default drawable
        }

        // Set a click listener for the item
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition) // Refresh previously selected item
            notifyItemChanged(selectedPosition) // Refresh newly selected item
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