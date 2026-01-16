package com.example.collegeplacementtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResourcesAdapter(
    private val resources: List<Resource>,
    private val onItemClick: (Resource) -> Unit
) : RecyclerView.Adapter<ResourcesAdapter.ResourceViewHolder>() {

    inner class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.resourceTitleText)
        val descriptionText: TextView = itemView.findViewById(R.id.resourceDescriptionText)
        val typeText: TextView = itemView.findViewById(R.id.resourceTypeText)

        fun bind(resource: Resource) {
            titleText.text = resource.title
            descriptionText.text = resource.description
            typeText.text = resource.type

            itemView.setOnClickListener {
                onItemClick(resource)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        holder.bind(resources[position])
    }

    override fun getItemCount(): Int = resources.size
}
