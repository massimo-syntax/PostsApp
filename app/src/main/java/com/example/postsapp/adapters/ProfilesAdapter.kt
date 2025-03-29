package com.example.postsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.postsapp.databinding.ItemProfileBinding
import com.example.postsapp.models.Profile

class ProfilesAdapter (private val profiles: MutableList<Profile>) :
    RecyclerView.Adapter<ProfilesAdapter.ViewHolder>() {

    private lateinit var binding: ItemProfileBinding

    private lateinit var ctx : Context
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(binding: ItemProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvName
        val image = binding.ivImage
        val nPosts = binding.tvNPosts

        init {
            // Define click listener for the ViewHolder's View
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        ctx = parent.context
        val itemBinding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.name.text = profiles[position].name
        if(!profiles[position].image.isNullOrEmpty()){
            Glide.with(ctx)
                .load(profiles[position].image)
                .into(viewHolder.image)
        }
        viewHolder.nPosts.text = profiles[position].nPosts.toString()

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = profiles.size

}