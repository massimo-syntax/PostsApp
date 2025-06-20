package com.example.postsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.postsapp.convertTimestampToReadableFormat
import com.example.postsapp.databinding.ItemCommentBinding
import com.example.postsapp.models.Both
import com.example.postsapp.models.Comment


class CommentsAdapter(private val comments: MutableList<Comment>, private val alreadyLikedComments: MutableSet<String>, private val myUserId:String , private val onItemClick: (Comment) -> Unit) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemCommentBinding , onItemClicked: (Int)->Unit) : RecyclerView.ViewHolder(binding.root) {
        val user = binding.tvUsername
        val dateTime = binding.tvDatetime
        val text = binding.tvText
        val btnOption = binding.btnCommentOption

        init {
            btnOption.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent ,false)
        return ViewHolder(itemBinding){
            onItemClick(comments[it])
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val comment = comments[position]
        holder.user.text = comment.userName
        val date = convertTimestampToReadableFormat(comment.dateTime!!.toLong())
        holder.dateTime.text = date
        holder.text.text = comment.comment


        if(comment.userId == myUserId){
            holder.btnOption.text = "Delete"
        }else{
            holder.btnOption.text = "Like"

            if(alreadyLikedComments.contains(comment.id)){
                holder.btnOption.text = "Unlike"
            }else{
                holder.btnOption.text = "Like"
            }

        }
    }

    override fun getItemCount() = comments.size

}