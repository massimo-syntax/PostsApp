package com.example.postsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.postsapp.databinding.ItemProfileBinding
import com.example.postsapp.models.Profile

class ProfilesAdapter (private val profiles: MutableList<Profile> , private val onItemClick: (Profile) -> Unit) :
    RecyclerView.Adapter<ProfilesAdapter.ViewHolder>() {

    private lateinit var binding: ItemProfileBinding

    private lateinit var ctx : Context
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(binding: ItemProfileBinding , onItemClicked: (Int)->Unit) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvName
        val image = binding.ivImage
        val nPosts = binding.tvNPosts
        val nFollowers = binding.tvNFollowers

        init {
            // Define click listener for the ViewHolder's View
            binding.root.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        ctx = parent.context
        val itemBinding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding){
            onItemClick(profiles[it])
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val item = profiles[position]

        viewHolder.name.text = item.name
        viewHolder.nPosts.text = item.postsCount.toString()
        viewHolder.nFollowers.text = item.followersCount.toString()

        if(!item.image.isNullOrEmpty()){
            Glide.with(ctx)
                .load(item.image)
                .into(viewHolder.image)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = profiles.size

}