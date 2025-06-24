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

        // create new rv only when there is data from firebase
        fun refreshProfilesRv(profiles:MutableList<Profile>){
            val rvProfiles = binding.rvProfiles
            rvProfiles.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapterProfiles = ProfilesAdapter(profiles){
                    profile ->
                val action = HomeFragmentDirections.actionHomeFragmentToProfileDetailsFragment(profile.uid!!)
                findNavController().navigate(action)
            }
            rvProfiles.adapter = adapterProfiles
        }

        // GET LIST OF      P R O F I L E
        profileViewModel.profilesList.observe(viewLifecycleOwner){
                profilesList ->
            if(profilesList == null) return@observe

            // navigating back <- to this fragment, even from menu, the rv has the double of items, repeated.
            /*
            adapterProfiles.notifyItemRangeRemoved(0,profiles.size)
            adapterProfiles.notifyItemRangeInserted(0,profiles.size)
            profiles.removeAll(profilesList)
            profiles.addAll(profilesList)
             */
            //is also needed a profileViewModel.clearProfileList(), even with the list assigned = not .add()

            // for this fragment this is the best solution
            refreshProfilesRv(profilesList.toMutableList())
        }
        // request profiles once from databse
        profileViewModel.getProfilesList()


        // create new rv only when there is data from db
        fun refreshPostsRv(posts:MutableList<Post>){
            val rvPost = binding.rvPosts
            rvPost.layoutManager = LinearLayoutManager(context)
            val adapterPost = PostsAdapter(posts){
                    post ->
                val action = HomeFragmentDirections.actionHomeFragmentToPostDetailsFragment(post.id!!)
                findNavController().navigate(action)
            }
            rvPost.adapter = adapterPost
        }

        // GET LIST OF      P O S T S
        postsViewmodel.postsList.observe(viewLifecycleOwner){
            postsList ->
            if(postsList == null) return@observe
            refreshPostsRv(postsList)
        }
        // request posts once from databse
        postsViewmodel.getPostsList()


    } /* onViewCreated */
    

}