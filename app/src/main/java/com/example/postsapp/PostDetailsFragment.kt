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
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.CommentsViewModel
import com.example.postsapp.viewModels.FragmentStateViewModel
import com.example.postsapp.viewModels.MainViewModel
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

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

        //  G E T     P R O F I L E
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

        //  P O S T     O B S E R V E R
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

            var tags = ""
            if( p.tags != null && !p.tags.isNullOrEmpty() ){
                p.tags!!.forEach { tag ->
                    tags += "#${tag.key}  "
                }
            }
            binding.tvTags.text = tags
            binding.tvLikes.text = p.likes!!.size.toString()
            //toast("observed $p")
            if(alreadyLiked()){
                binding.btnLike.setImageResource(R.drawable.like)
            }
            profileViewModel.getSingleProfile(post!!.userId!!)
        }
        postViewModel.getCurrentPost(postId!!)

        //  C O M M E N T S      R E C Y C L E R     V I E W
        fun commentAlreadyLiked(id:String) : Boolean {
            val likedCommentsMap = profileViewModel.myProfile.value!!.likedComments
            return likedCommentsMap!!.containsKey(id)
        }

        val rvComments = binding.rvComments
        rvComments.layoutManager = LinearLayoutManager(context)
        // used to choose the icon
        val alreadyLikedComments = mutableSetOf<String>()

        val adapterComments = CommentsAdapter(
            commentsViewModel.allComments,
            alreadyLikedComments,
            commentsViewModel.commentsUsers,
            profileViewModel.currentUID
        ){
            comment ->
            // the COMMENT is YOURS, you can delete the comment
            if(comment.userId == profileViewModel.currentUID){
                commentsViewModel.deleteComment(comment , postId!! , profileViewModel.currentUID)
            }else{
                // SOMEONE ELSE S COMMENT, you can like
                if(commentAlreadyLiked(comment.id!!)){
                    commentsViewModel.unlikeComment(profileViewModel.currentUID , comment.id!!,postId!!)
                }else{
                    commentsViewModel.likeComment(profileViewModel.currentUID, comment.id!! , postId!!)
                }
            }
        }
        rvComments.adapter = adapterComments


        //  C O M M E N T S       E V E N T S
        // every time that firebase adds a new comment, runs the event listener
        // the same event listener runs for every comment 1 by 1 at registerCommentsEventListenerForThisPost(postId!!)
        // view model -> firebase listener
        commentsViewModel.event.observe(viewLifecycleOwner){ e ->
            if (e == null) return@observe
            when( e.first ){
                "info" ->{
                    val commentId:String = e.second
                    if(commentAlreadyLiked(commentId)) alreadyLikedComments.add(commentId)
                    val index = commentsViewModel.allComments.indexOfFirst{ it.id == commentId }
                    adapterComments.notifyItemChanged(index)
                }
                "added" ->{
                    val commentId:String = e.second
                    if(commentAlreadyLiked(commentId)) alreadyLikedComments.add(commentId)
                    val index = commentsViewModel.allComments.indexOfFirst{ it.id == commentId }
                    adapterComments.notifyItemInserted(index)
                }
                "removed" -> {
                    val index:Int = e.second.toInt()
                    adapterComments.notifyItemRemoved(index)
                }
                "liked" -> {
                    val commentId:String = e.second
                    val index = commentsViewModel.allComments.indexOfFirst{ it.id == commentId }
                    // ADD TO local data, is not updated the whole comment in firebase, no listener.
                    profileViewModel.myProfile.value!!.likedComments!![commentId] = postId!!
                    adapterComments.notifyItemChanged(index)
                }
                "unliked" -> {
                    val commentId:String = e.second
                    val index = commentsViewModel.allComments.indexOfFirst{ it.id == commentId }
                    // REMOVE FROM local data, is not updated the whole comment in firebase, no listener.
                    profileViewModel.myProfile.value!!.likedComments!!.remove(commentId)
                    adapterComments.notifyItemChanged(index)
                }
                else -> toast("event fired, e.first is not added")
            }
        }
        // the event listener also requests every comment 1 by 1 when started
        commentsViewModel.registerCommentsEventListenerForThisPost(postId!!)

        //  W R I T E       C O M M E N T
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

        //  L I K E     C O M M E N T
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