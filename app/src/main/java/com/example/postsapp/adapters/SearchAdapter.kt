package com.example.postsapp.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.ITALIC
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.postsapp.QueryString
import com.example.postsapp.R
import com.example.postsapp.convertTimestampToReadableFormat
import com.example.postsapp.databinding.ItemSearchBinding
import com.example.postsapp.models.Both
import java.util.Locale

class SearchAdapter (private val both: MutableList<Both> , private var query:QueryString , private val onItemClick: (Both) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var ctx : Context

    private fun highlightText(text: String, filter: String): Spannable? {
        //val startPos: Int = text.lowercase(Locale.getDefault()).indexOf(filter.lowercase(Locale.getDefault()))
        //val endPos: Int = startPos + filter.length

        val queryFoundAt = mutableListOf<Int>()
        var idx = text.lowercase(Locale.getDefault()).indexOf(filter.lowercase(Locale.getDefault()))
        while (idx != -1) {
           queryFoundAt.add(idx)
            idx = text.lowercase(Locale.getDefault()).indexOf(filter.lowercase(Locale.getDefault()) , idx + 1 )
        }

        //if (startPos != -1) {
        if (queryFoundAt.isNotEmpty()) {
            val spannable: Spannable = SpannableString(text)
            // val colorStateList = resourceProvider.getColorStateList(R.color.teal_200)
            // val textAppearanceSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, colorStateList, null)

            for(index in queryFoundAt){
                spannable.setSpan(
                    StyleSpan(ITALIC),
                    index,
                    index + filter.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ctx.getColor(R.color.blue)),
                    index,
                    index + filter.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    StyleSpan(BOLD),
                    index,
                    index + filter.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return spannable
        }
        return null
    }




    class ViewHolder(binding: ItemSearchBinding , onItemClicked: (Int)->Unit ) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvTitle
        val image = binding.ivImage
        val description = binding.tvDescription
        val datetime = binding.tvDatetime
        val count = binding.tvCount

        init {
            binding.root.setOnClickListener{
                onItemClicked(adapterPosition)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        ctx = parent.context
        val itemBinding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding){
            onItemClick(both[it])
        }

    }



    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val item = both[position]

        val query = query.s

        // higligting TITLE
        if(item.title.contains(query) && query.isNotEmpty()){
            viewHolder.name.text = highlightText( item.title , query )//highlightText(item.title , query)
        }else{
            viewHolder.name.text = item.title
        }

        // highlight DESCRIPTION
        if(item.description.contains(query) && query.isNotEmpty()){
            viewHolder.description.text = highlightText( item.description , query )//highlightText(item.title , query)
        }else{
            viewHolder.description.text = item.description
        }

        var time:Long = 0
        if(!item.dateTime.isNullOrEmpty()) time = item.dateTime.toLong()
        viewHolder.datetime.text = convertTimestampToReadableFormat(time)
        viewHolder.count.text = item.count.toString()

        if(!item.image.isNullOrEmpty()){
            Glide.with(ctx)
                .load(item.image)
                .into(viewHolder.image)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = both.size

}