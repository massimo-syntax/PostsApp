package com.example.postsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.postsapp.R
import com.example.postsapp.databinding.ItemImageBinding

class PicturesAdapter( private val pictures: MutableList<String> , private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<PicturesAdapter.ViewHolder>() {

    private lateinit var ctx : Context
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(binding: ItemImageBinding, onItemClicked: (Int)->Unit) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivImage
        val deleteBtn = binding.btnDelete

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
        val itemBinding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding){ index ->
            onItemClick(index)
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val picture = pictures[position]
            Glide.with(ctx)
                .load(picture)
                .into(viewHolder.img)
            viewHolder.deleteBtn.setImageResource(R.drawable.delete)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = pictures.size

}