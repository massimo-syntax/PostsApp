package com.example.postsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.postsapp.R
import com.example.postsapp.convertTimestampToReadableFormat
import com.example.postsapp.databinding.ItemCommentBinding
import com.example.postsapp.models.Both
import com.example.postsapp.models.Comment
import com.example.postsapp.models.Profile


class CommentsAdapter(private val comments: MutableList<Comment>, private val alreadyLikedComments: MutableSet<String>, private val commentsUsers:MutableSet<Profile>, private val myUserId:String , private val onItemClick: (Comment) -> Unit) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

        private lateinit var ctx:Context

    class ViewHolder(binding: ItemCommentBinding , onItemClicked: (Int)->Unit) : RecyclerView.ViewHolder(binding.root) {
        val user = binding.tvUsername
        val dateTime = binding.tvDatetime
        val text = binding.tvText
        val btnOption = binding.btnCommentOption
        val likesCount = binding.likesCount
        val image = binding.ivImage

        init {
            btnOption.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ctx = parent.context
        val itemBinding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent ,false)
        return ViewHolder(itemBinding){
            onItemClick(comments[it])
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val comment = comments[position]

        val userIndex = commentsUsers.indexOfFirst { it.uid == comment.userId }
        var userName = ""
        var userImage = ""
        if (userIndex != -1){
            val user = commentsUsers.elementAt(userIndex)
            userName = user.name ?: ""
            userImage = user.image ?: ""
            if (! user.image.isNullOrEmpty() ) Glide.with(ctx).load(userImage).into(holder.image)
        }


        holder.user.text = comment.userName

        val date = convertTimestampToReadableFormat(comment.dateTime!!.toLong())
        holder.dateTime.text = date
        holder.text.text = comment.comment
        holder.likesCount.text = comment.likesCount.toString()


        // icon button
        if(comment.userId == myUserId){
            holder.btnOption.setImageResource( R.drawable.delete )
        }else{
            if(alreadyLikedComments.contains(comment.id)){
                holder.btnOption.setImageResource( R.drawable.like )
            }else{
                holder.btnOption.setImageResource( R.drawable.unliked )
            }
        }
    }

    override fun getItemCount() = comments.size

}