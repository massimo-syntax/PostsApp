package com.example.postsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.postsapp.convertTimestampToReadableFormat
import com.example.postsapp.databinding.ItemPostBinding
import com.example.postsapp.models.Post

class PostsAdapter(private val posts: MutableList<Post> ,  private val onItemClick: (Post) -> Unit) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private lateinit var ctx : Context

    class ViewHolder(binding: ItemPostBinding , onItemClicked: (Int)->Unit) : RecyclerView.ViewHolder(binding.root) {
        val user = binding.tvUser
        val time = binding.tvTime
        val title = binding.tvTitle
        val body = binding.tvBody
        val image = binding.ivImage
        val likesCount = binding.tvLikesCount
        val tags = binding.tvTags

        init {
            // Define click listener for the ViewHolder's View
            binding.root.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ctx = parent.context
        // Create a new view, which defines the UI of the list item
        val itemBinding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding){
            onItemClick(posts[it])
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val post = posts[position]

        viewHolder.user.text = post.user
        viewHolder.time.text = convertTimestampToReadableFormat(post.datetime!!.toLong())
        viewHolder.title.text = post.title

        var body = post.body ?: ""
        if( body.length > 170) body = post.body!!.slice(0..170) + "..."
        viewHolder.body.text = body

        var tags = ""
        if( post.tags != null && !post.tags.isNullOrEmpty() ){
            post.tags!!.forEach { tag ->
                tags += "#${tag.key}  "
            }
        }

        viewHolder.tags.text = tags

        viewHolder.likesCount.text = post.likesCount.toString()

        if(!post.image.isNullOrEmpty()){
            Glide.with( ctx )
                .load(post.image)
                .into(viewHolder.image)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = posts.size

}