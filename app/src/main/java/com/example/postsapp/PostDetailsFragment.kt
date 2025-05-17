package com.example.postsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postsapp.adapters.CommentsAdapter
import com.example.postsapp.databinding.FragmentPostDetailsBinding
import com.example.postsapp.models.Comment
import com.example.postsapp.models.Post
import com.example.postsapp.viewModels.CommentsViewModel
import com.example.postsapp.viewModels.PostViewModel


class PostDetailsFragment : Fragment() {

    private var postId:String? = null

    private lateinit var binding : FragmentPostDetailsBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var commentsViewModel: CommentsViewModel

    private fun toast(s:String){
        Toast.makeText(context,s, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString("postId")
        }
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        commentsViewModel = ViewModelProvider(this)[CommentsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_post_details, container, false)
        binding = FragmentPostDetailsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPostId.text = postId
        var post: Post? = null
        var likes = 0

        fun alreadyLiked():Boolean{
            return post!!.likes!!.containsKey(postViewModel.currentUID)
        }

        postViewModel.currentPost.observe(viewLifecycleOwner){ p ->
            if(p == null) return@observe
            post = p
            binding.tvPostTitle.text = p.title + " from " + p.user
            binding.tvPostBody.text = p.body
            likes = p.likes!!.size
            binding.tvLikes.text = likes.toString()
            toast("observed $p")
            if(alreadyLiked()){
                binding.btnLike.text = "unlike"
            }

        }
        postViewModel.getCurrentPost(postId!!)

        val comments = mutableListOf<Comment>()

        val rvComments = binding.rvComments
        rvComments.layoutManager = LinearLayoutManager(context)
        val adapterComments = CommentsAdapter(comments)
        rvComments.adapter = adapterComments

        var formShowing = false

        fun toggleForm(){
            if(formShowing){
                formShowing = false
                binding.btnShowForm.text = "write comment"
                binding.commentForm.visibility = View.GONE
            }else{
                formShowing = true
                binding.btnShowForm.text = "writing comment .."
                binding.commentForm.visibility = View.VISIBLE
            }
            binding.etWriteComment.setText("")
        }

        commentsViewModel.event.observe(viewLifecycleOwner){e->
            if (e == null) return@observe
            when(e.first){
                "added" -> toast(e.second)
                else -> toast("event fired, e.first is not added")
            }
        }


        commentsViewModel.allPostsComments.observe(viewLifecycleOwner){ list ->
            if (list == null ) return@observe
            toast(list.toString())
        }

        commentsViewModel.getAllComments(postId!!)
        commentsViewModel.registerPostCommentsEventListener(postId!!)


        binding.btnShowForm.setOnClickListener{
            toggleForm()
        }

        binding.btnWriteComment.setOnClickListener{
            if (binding.etWriteComment.text.isNullOrBlank()) return@setOnClickListener

            val newComment = Comment(
                System.currentTimeMillis().toString(),"username","useridform", binding.etWriteComment.editableText.toString().trim(),"datetime", mutableMapOf()
            )

            comments.add(newComment)
            adapterComments.notifyItemInserted(comments.size-1)
            toggleForm()

            // check whats with firebase
            commentsViewModel.writeComment(newComment,postId!!)


        }




        // the observer changes already the value of the live data profile
        binding.btnLike.setOnClickListener {
            if(!alreadyLiked()){
                postViewModel.likePost()
                likes++
                binding.btnLike.text = "unlike"
            }else{
                postViewModel.unlikePost()
                likes--
                binding.btnLike.text = "like"
            }
            binding.tvLikes.text = likes.toString()
        }







    }


}