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
        // for now just count.. users can also comment, in future updates of the app may be also see which users liked..
        // being an app for posts can be nice to like without that your like is seen..
        // in every case user can also comment posts then they can say something directly there..
        var likesCount = 0

        fun alreadyLiked():Boolean{
            return post!!.likes!!.containsKey(postViewModel.currentUID)
        }

        postViewModel.currentPost.observe(viewLifecycleOwner){ p ->
            if(p == null) return@observe
            post = p
            binding.tvPostTitle.text = p.title + " from " + p.user
            binding.tvPostBody.text = p.body
            likesCount = p.likes!!.size
            binding.tvLikes.text = likesCount.toString()
            toast("observed $p")
            if(alreadyLiked()){
                binding.btnLike.text = "unlike"
            }

        }
        postViewModel.getCurrentPost(postId!!)


        val rvComments = binding.rvComments
        rvComments.layoutManager = LinearLayoutManager(context)
        // adapter has the comments from viewmodel
        val adapterComments = CommentsAdapter( commentsViewModel.allComments )
        rvComments.adapter = adapterComments


/*      PROBABLY NO NEED..
        SINGLE COMMENT EVENT LISTENER REQUESTS TO FIREBASE EVERY COMMENT 1 BY 1
        CAN BE THAT WITH RECYCLERVIEW ADD ELEMENTS 1 BY 1 IS VERY PRACTICAL ....
        commentsViewModel.allPostsComments.observe(viewLifecycleOwner){ list ->
            if (list == null ) return@observe
            toast(list.toString())
        }
  */
        // list of comment
        //commentsViewModel.getAllComments(postId!!)

        // every time that firebase adds new comment,
        // also when requested first,
        // the callback for event is run
        var lastIndex = 0
        commentsViewModel.event.observe(viewLifecycleOwner){e->
            if (e == null) return@observe
            when(e.first){
                "added" ->{
                    // been loaded first when started the event listener
                    // then every time that firebase receives a new comment
                    toast(e.second + "  at last index [$lastIndex]")
                    // the viewmodel adds the comment from databse in the list
                    // then calls the observer
                    // the list is in the viewmodel, just for avoid cpu overload th index is just increased instead to count all the list every comment added
                    // comments callback is from firebase, then is everything already nice and syncronous
                    adapterComments.notifyItemInserted(lastIndex)
                    lastIndex++
                }

                else -> toast("event fired, e.first is not added")
            }
        }

        // event listener to comments of this postId
        // the event listener also requests every comment 1 by 1 when started
        commentsViewModel.registerPostCommentsEventListener(postId!!)


        // WRITE COMMENT
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
        // btn toggle form
        binding.btnShowForm.setOnClickListener{
            toggleForm()
        }
        // btn write
        binding.btnWriteComment.setOnClickListener{
            if (binding.etWriteComment.text.isNullOrBlank()) return@setOnClickListener
            val dateTime = System.currentTimeMillis().toString()

            val newComment = Comment(
                id = dateTime,
                userName = postViewModel.myProfile!!.name,
                userId = postViewModel.myProfile!!.uid,
                comment = binding.etWriteComment.editableText.toString().trim(),
                dateTime = dateTime,
                likes = mutableMapOf()
            )

            //comments.add(newComment)
            //adapterComments.notifyItemInserted(comments.size-1)
            toggleForm()

            // send to database
            commentsViewModel.writeComment(newComment,postId!!)

        }
        // WRITE COMMENT [ E N D ]

        // LIKE
        binding.btnLike.setOnClickListener {
            if(!alreadyLiked()){
                postViewModel.likePost()
                likesCount++
                binding.btnLike.text = "unlike"
            }else{
                postViewModel.unlikePost()
                likesCount--
                binding.btnLike.text = "like"
            }
            binding.tvLikes.text = likesCount.toString()
        }







    }


}