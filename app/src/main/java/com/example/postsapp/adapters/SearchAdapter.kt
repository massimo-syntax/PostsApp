package com.example.postsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.postsapp.databinding.ItemSearchBinding
import com.example.postsapp.models.Both

class SearchAdapter (private val both: MutableList<Both>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var binding: ItemSearchBinding

    private lateinit var ctx : Context
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvTitle
        val image = binding.ivImage
        val description = binding.tvDescription

        init {
            // Define click listener for the ViewHolder's View
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        ctx = parent.context
        val itemBinding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.name.text = both[position].title
        if(!both[position].image.isNullOrEmpty()){
            Glide.with(ctx)
                .load(both[position].image)
                .into(viewHolder.image)
        }
        viewHolder.description.text = both[position].toString()

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = both.size

}