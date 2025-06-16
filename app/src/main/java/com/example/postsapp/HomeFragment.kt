package com.example.postsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postsapp.adapters.PostsAdapter
import com.example.postsapp.adapters.ProfilesAdapter
import com.example.postsapp.databinding.FragmentHomeBinding
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var postsViewmodel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // no  params
        }
        // init viewmodels
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        postsViewmodel = ViewModelProvider(this)[PostViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  profiles recycler view
        val profiles = mutableListOf<Profile>()
        val rvProfiles = binding.rvProfiles
        rvProfiles.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapterProfiles = ProfilesAdapter(profiles){
            profile ->
            val action = HomeFragmentDirections.actionHomeFragmentToProfileDetailsFragment(profile.uid!!)
            findNavController().navigate(action)
        }
        rvProfiles.adapter = adapterProfiles

        // GET LIST OF      P R O F I L E
        profileViewModel.profilesList.observe(viewLifecycleOwner){
                profilesList ->
            // when first viewmodel is init() allPosts is null
            // navigating back to this fragment the observer every time requests the data from database,
            // the adapter has his list on place, the rv flicks, and there is a duplicate of data in the rv
            // this is the best solution for now, no new data in the database, no flickering duplicates
            if(profilesList == null || adapterProfiles.itemCount == profilesList.size) return@observe

            profiles.removeAll(profilesList)
            profiles.addAll(profilesList)
            // here the adapter receives a new list only once
            // once when the databse is queryed first, never more..
            // the livedata.value just changes from null to the list 1 time
            adapterProfiles.notifyItemRangeRemoved(0,profiles.size)
            adapterProfiles.notifyItemRangeInserted(0,profiles.size)
        }
        // request profiles once from databse
        profileViewModel.getProfilesList()


        // posts recycler view
        var posts = mutableListOf<Post>()
        val rvPost = binding.rvPosts
        rvPost.layoutManager = LinearLayoutManager(context)
        val adapterPost = PostsAdapter(posts){
            post ->
            val action = HomeFragmentDirections.actionHomeFragmentToPostDetailsFragment(post.id!!)
            findNavController().navigate(action)
        }
        rvPost.adapter = adapterPost

        // GET LIST OF      P O S T S
        postsViewmodel.postsList.observe(viewLifecycleOwner){
            postsList ->
            // when first viewmodel is init() allPosts is null
            // navigating back to this fragment the observer every time requests the data from database,
            // the adapter has his list on place, the rv flicks, and there is a duplicate of data in the rv
            // this is the best solution for now, no new data in the database, no flickering duplicates
            if(postsList == null || adapterPost.itemCount == postsList.size) return@observe

            posts.removeAll(postsList)
            posts.addAll(postsList)
            // here the adapter receives a new list only once
            // once when the databse is queryed first, never more..
            // the livedata.value just changes from null to the list 1 time
            adapterPost.notifyItemRangeRemoved(0,posts.size)
            adapterPost.notifyItemRangeInserted(0,posts.size)
        }
        // request posts once from databse
        postsViewmodel.getPostsList()


    } /* onViewCreated */


}