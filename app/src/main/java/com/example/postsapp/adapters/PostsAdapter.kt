package com.example.postsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.postsapp.databinding.ItemPostBinding
import com.example.postsapp.models.Post


class PostsAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {


    class ViewHolder(binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        val user = binding.tvUser
        val title = binding.tvTitle
        val body = binding.tvBody

        init {
            // Define click listener for the ViewHolder's View
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val itemBinding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.user.text = posts[position].user
        viewHolder.title.text = posts[position].title
        viewHolder.body.text = posts[position].body

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = posts.size

}