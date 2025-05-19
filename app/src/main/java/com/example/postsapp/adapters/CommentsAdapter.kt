package com.example.postsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.postsapp.convertTimestampToReadableFormat
import com.example.postsapp.databinding.ItemCommentBinding
import com.example.postsapp.models.Comment


class CommentsAdapter(private val comments: MutableList<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {


    class ViewHolder(binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val user = binding.tvUsername
        val dateTime = binding.tvDatetime
        val text = binding.tvText

        init {
            // Define click listener for the ViewHolder's View
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent ,false)
        return ViewHolder(itemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val comment = comments[position]
        holder.user.text = comment.userName
        val date = convertTimestampToReadableFormat(comment.dateTime!!.toLong())
        holder.dateTime.text = date
        holder.text.text = comment.comment
    }

    override fun getItemCount() = comments.size

}