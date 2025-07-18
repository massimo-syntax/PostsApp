package com.example.postsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.postsapp.adapters.CommentsAdapter
import com.example.postsapp.databinding.FragmentPostDetailsBinding
import com.example.postsapp.models.Comment
import com.example.postsapp.models.Post
import com.example.postsapp.viewModels.CommentsViewModel
import com.example.postsapp.viewModels.FragmentStateViewModel
import com.example.postsapp.viewModels.MainViewModel
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel

class PostDetailsFragment : Fragment() {

    private var postId:String? = null

    private lateinit var binding : FragmentPostDetailsBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var commentsViewModel: CommentsViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var fragmentState:FragmentStateViewModel

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
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        fragmentState = ViewModelProvider(this)[FragmentStateViewModel::class.java]
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

        var post: Post? = null
        // for now just count.. users can also comment, in future updates of the app may be also see which users liked..
        // being an app for posts can be nice to like without that your like is seen..
        // in every case user can also comment posts then they can say something directly there..
        var likesCount = 0

        fun alreadyLiked():Boolean{
            return post!!.likes!!.containsKey(profileViewModel.currentUID)
        }

        profileViewModel.singleProfile.observe(viewLifecycleOwner){ profile ->
            if(profile == null) return@observe
            if( ! profile.image.isNullOrEmpty() ) {
                Glide.with(requireContext())
                    .load(profile.image)
                    .into(binding.ivAuthorImage)
            }
            binding.tvAuthorUsername.text = profile.name
            binding.tvPostsCount.text = profile.postsCount.toString()
            binding.tvFollowersCount.text = profile.followersCount.toString()

            fun goToProfile(){
                val action = PostDetailsFragmentDirections.actionPostDetailsFragmentToProfileDetailsFragment(profile.uid!!)
                findNavController().navigate(action)
            }
            binding.ivAuthorImage.setOnClickListener { goToProfile() }
            binding.tvAuthorUsername.setOnClickListener { goToProfile() }


        } // profile is requested when the post is loaded



        postViewModel.currentPost.observe(viewLifecycleOwner){ p ->
            if(p == null) return@observe
            post = p
            if( ! p.image.isNullOrEmpty() ) {
                Glide.with(requireContext())
                    .load(p.image)
                    .into(binding.ivPostImage)
            }
            binding.tvPostTitle.text = p.title
            binding.tvDatetime.text = convertTimestampToReadableFormat(p.datetime!!.toLong())
            binding.tvPostBody.text = p.body
            likesCount = p.likes!!.size
            binding.tvLikes.text = likesCount.toString()
            //toast("observed $p")
            if(alreadyLiked()){
                binding.btnLike.setImageResource(R.drawable.like)
            }
            profileViewModel.getSingleProfile(post!!.userId!!)
        }
        postViewModel.getCurrentPost(postId!!)


        val alreadyLikedComments = mutableSetOf<String>()
        fun commentAlreadyLiked(id:String) : Boolean {
            val likedCommentsMap = profileViewModel.myProfile.value!!.likedComments
            return likedCommentsMap!!.containsKey(id)
        }



        val rvComments = binding.rvComments
        rvComments.layoutManager = LinearLayoutManager(context)
        // adapter has the comments from viewmodel
        val adapterComments = CommentsAdapter( commentsViewModel.allComments, alreadyLikedComments, profileViewModel.currentUID ){
            comment ->
            // the COMMENT is YOURS, you can delete the comment
            if(comment.userId == profileViewModel.currentUID){
                // DELETE COMMENT (if yours)
                //toast("commented from you -> delete")
                commentsViewModel.deleteComment(comment , postId!! , profileViewModel.currentUID)
                //toast("deleting comment ${comment.userId}")

            }else{ // SOMEONE ELSE S COMMENT, you can like
                /*
                if(commentAlreadyLiked(comment.id!!)){
                    commentsViewModel.unlikeComment(profileViewModel.currentUID , comment.id!!,postId!!)
                }else{
                    commentsViewModel.likeComment(profileViewModel.currentUID, comment.id!! , postId!!)
                }

                 */
            }
        }
        rvComments.adapter = adapterComments



        // every time that firebase adds a new comment, runs the event listener
        // the same event listener runs for every comment 1 by 1 at first request
        // that is in the viewmodel, there is notified also the event live data, that calls this observer
        //var lastIndex = 0
        commentsViewModel.event.observe(viewLifecycleOwner){e->
            if (e == null) return@observe
            when( e.first ){
                "added" ->{
                    // been loaded first when started the event listener
                    // then every time that firebase receives a new comment
                    toast(e.second + "  at last index [${commentsViewModel.allComments.size-1}]")
                    // the viewmodel adds the comment from databse in the list
                    // then calls the observer
                    // the list is in the viewmodel, just for avoid cpu overload th index is just increased instead to count all the list every comment added

                    if(commentAlreadyLiked(e.second)){
                        // add to list for adapter, already 1 by 1 receiving from database
                        alreadyLikedComments.add(e.second)
                    }
                    adapterComments.notifyItemInserted(commentsViewModel.allComments.size-1)
                }
                "removed" -> {
                    toast("index = ${e.second}")
                    adapterComments.notifyItemRemoved(e.second.toInt())
                }
                "liked" -> {
                    toast("liked comment id ${e.second}")
                    // @ todo
                    // add to likedcomments list and map
                    // notify adapter
                }
                "unliked" -> {
                    toast("unliked comment id ${e.second}")
                    // @todo
                    // add to likedcomments list and map
                    // notify adapter
                }
                else -> toast("event fired, e.first is not added")
            }
        }

        // event listener to comments of this postId
        // the event listener also requests every comment 1 by 1 when started
        commentsViewModel.registerCommentsEventListenerForThisPost(postId!!)


        // WRITE COMMENT
        var formShowing = false

        fun toggleForm(){
            if(formShowing){
                formShowing = false
                binding.btnShowForm.text = "write comment"
                binding.commentForm.visibility = View.GONE
            }else{
                formShowing = true
                binding.btnShowForm.text = "cancel"
                binding.commentForm.visibility = View.VISIBLE
            }
            binding.etWriteComment.setText("")
        }
        // btn toggle form
        binding.btnShowForm.setOnClickListener{
            toggleForm()
        }
        // BTN      S U B M I T  comment
        binding.btnWriteComment.setOnClickListener{
            if (binding.etWriteComment.text.isNullOrBlank()) return@setOnClickListener
            val dateTime = System.currentTimeMillis().toString()

            val newComment = Comment(
                id = "from firebase",
                userName = profileViewModel.myProfile.value!!.name,
                userId = profileViewModel.myProfile.value!!.uid,
                comment = binding.etWriteComment.editableText.toString().trim(),
                dateTime = dateTime,
                likesCount = 0
            )

            // send to database
            commentsViewModel.writeComment(newComment,postId!!, profileViewModel.currentUID)
            toggleForm()

        }
        // WRITE COMMENT [ E N D ]

        // LIKE
        binding.btnLike.setOnClickListener {
            if(!alreadyLiked()){
                postViewModel.likePost(profileViewModel.currentUID)
                likesCount++
                binding.btnLike.setImageResource(R.drawable.like)
            }else{
                postViewModel.unlikePost(profileViewModel.currentUID)
                likesCount--
                binding.btnLike.setImageResource(R.drawable.unliked)
            }
            binding.tvLikes.text = likesCount.toString()
        }



    }



    // some fragments are used navigating from different tabs of bottom navbar
    // like that, after the user was navigating around, when pressed on a bottom tab
    // if this fragment is still on the stack of the previous navigation, gets popd bach to the proper root fragment, of the tab.
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onResume() {
        super.onResume()
        // every click on something that navigates, has to be set the last tab pressed
        if( fragmentState.lastTabPressed == ""){
            // fragment is fresh on the stack
            fragmentState.lastTabPressed = mainViewModel.currentSection!! /*this fragment is accessed never directly from menu*/
        }
        // as previous statement, only when the fragment is fresh on the stack gets assigned the root-tab-selected
        // let say that in between the user pressed another tab, then gets back to this again
        // the fragment has already an instance of viewmodel fragemntState, then changes only in this case
        if(fragmentState.lastTabPressed != mainViewModel.currentSection!!) findNavController().popBackStack()

        mainViewModel.setActionBarTitle("Post details")

        // avoid duplicates when getting back..
        commentsViewModel.allComments.removeAll(commentsViewModel.allComments)
    }









}