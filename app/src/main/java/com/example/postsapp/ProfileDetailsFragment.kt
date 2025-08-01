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
import com.example.postsapp.adapters.PostsAdapter
import com.example.postsapp.adapters.ProfilesAdapter
import com.example.postsapp.databinding.FragmentProfileDetailsBinding
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.FragmentStateViewModel
import com.example.postsapp.viewModels.MainViewModel
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel

class ProfileDetailsFragment : Fragment() {

    private var profileId: String? = null

    private lateinit var binding: FragmentProfileDetailsBinding
    private lateinit var profilesViewModel: ProfileViewModel
    private lateinit var postsViewModel: PostViewModel

    private lateinit var fragmentState: FragmentStateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            profileId = it.getString("profileId")
        }
        fragmentState = ViewModelProvider(this)[FragmentStateViewModel::class.java]
        profilesViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        postsViewModel = ViewModelProvider(this)[PostViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var profile: Profile? = null

        // init text waiting for server response
        // useful in the case that no profile is created yet
        binding.profileName.text = ""
        binding.sentence.text = ""
        binding.tvFollowersCount.text = ""



        fun alreadyFollowed(): Boolean {
            return profile!!.followers!!.containsKey(profilesViewModel.myProfile.value!!.uid.toString())
        }

        //      F O L L O W E D
        //  rv
        val rvFollowers = binding.rvFollowers
        rvFollowers.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        // adapter + click
        val followersList = mutableListOf<Profile>()
        val adapterFollowers = ProfilesAdapter( followersList ){ follower ->
            val action = ProfileDetailsFragmentDirections.actionProfileDetailsFragmentSelf(follower.uid!!)
            findNavController().navigate(action)
        }
        rvFollowers.adapter = adapterFollowers

        profilesViewModel.followersList.observe(viewLifecycleOwner) { followers ->
            if(followers == null) return@observe
            val count = followersList.size
            followersList.removeAll(followersList)
            adapterFollowers.notifyItemRangeRemoved(0, count)
            followersList.addAll(followers)
            adapterFollowers.notifyItemRangeInserted(0,followersList.size)
        }
        //  profilesViewModel.getFollowerListOf(p) is done first when the profile is loaded from db
        // in profile observer '''''''''v

        // declare rv
        // RV FOR POSTS OF THIS UID
        val rvPosts = binding.rvPosts
        rvPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val posts = mutableListOf<Post>()
        val adapterPosts = PostsAdapter(posts) { post ->
            val action = ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToPostDetailsFragment(post.id!!)
            findNavController().navigate(action)
        }
        rvPosts.adapter = adapterPosts

        // add to adapter every post queried
        postsViewModel.postsList.observe(viewLifecycleOwner) { postsList ->
            // in viewmodel starting value is null
            if (postsList == null) return@observe
            posts.removeAll(posts)
            adapterPosts.notifyItemRangeRemoved(0, posts.size)
            posts.addAll(postsList.reversed())
            adapterPosts.notifyItemRangeChanged(0, postsList.size)
        }
        // when this single profile is loaded from database the posts are also requested
        // see singleProfile.observe ----v


        var followers = 0
        var myProfile : Profile ? = null

        profilesViewModel.myProfile.observe(viewLifecycleOwner){ myProfileFromDB ->
            if (myProfileFromDB == null) return@observe
            myProfile = myProfileFromDB
        }
        profilesViewModel.getMyProfile()


        // btn follow profile
        binding.btnFollow.setOnClickListener {
            // dont follow yourself
            if (profile!!.uid!! == profilesViewModel.currentUID) return@setOnClickListener
            if (!alreadyFollowed()) {
                // follow !!
                profilesViewModel.likeProfile(profile!!.uid!!)
                followers++
                binding.tvFollowersCount.text = followers.toString()
                binding.btnFollow.setImageResource(R.drawable.like)
                // add to rv
                followersList.add(0, myProfile!!)
                adapterFollowers.notifyItemInserted(0)
            } else {
                // unfollow :(
                profilesViewModel.unlikeProfile(profile!!.uid!!)
                followers--
                binding.tvFollowersCount.text = followers.toString()
                binding.btnFollow.setImageResource(R.drawable.unliked)
                // find my profile
                val index = followersList.indexOfFirst { it.uid == myProfile!!.uid }
                followersList.removeAt(index)
                // remove from rv
                adapterFollowers.notifyItemRemoved(index)
            }
        }


        // REQUESTING    P R O F I L E   FROM DB
        profilesViewModel.singleProfile.observe(viewLifecycleOwner) { p ->
            if (p == null) return@observe
            profile = p
            followers = p.followersCount!!
            binding.tvPostsCount.text = p.postsCount.toString()
            binding.tvFollowersCount.text = followers.toString()
            binding.profileName.text = p.name
            binding.sentence.text = p.say
            if (!p.image.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(p.image)
                    .into(binding.ivImage)
            }
            if (alreadyFollowed()) {
                binding.btnFollow.setImageResource(R.drawable.like)
            }
            // make a list of posts from this profile
            //if (p.myPosts.isNullOrEmpty()) return@observe
            postsViewModel.getPostListFromUser(p.uid!!)
            profilesViewModel.getFollowerListOf(p)
        }
        // request profile
        profilesViewModel.getSingleProfile(profileId!!)

    }


    // some fragments are used navigating from different tabs of bottom navbar
    // like that, after the user was navigating around, when pressed on a bottom tab
    // if this fragment is still on the stack of the previous navigation, gets popd bach to the proper root fragment, of the tab.

    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onResume() {

        super.onResume()
        // every click on something that navigates, has to be set the last tab pressed
        if (fragmentState.lastTabPressed == "") {
            // fragment is fresh on the stack
            fragmentState.lastTabPressed =
                mainViewModel.currentSection!! /*this fragment is accessed never directly from menu*/
        }
        // as previous statement, only when the fragment is fresh on the stack gets assigned the root-tab-selected
        // let say that in between the user pressed another tab, then gets back to this again
        // the fragment has already an instance of viewmodel fragemntState, then changes only in this case
        if (fragmentState.lastTabPressed != mainViewModel.currentSection!!) findNavController().popBackStack()

        mainViewModel.setActionBarTitle("Profile details")

    }


}