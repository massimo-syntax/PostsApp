package com.example.postsapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postsapp.adapters.PostsAdapter
import com.example.postsapp.adapters.ProfilesAdapter
import com.example.postsapp.databinding.FragmentHomeBinding
import com.example.postsapp.models.Draft
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.SharedViewModel


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel : SharedViewModel

    private lateinit var ctx : Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // no  params
        }
        ctx = requireContext()
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
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

        val posts = mutableListOf<Post>()

        val rvPost = binding.rvPosts
        rvPost.layoutManager = LinearLayoutManager(ctx)
        val adapterPost = PostsAdapter(posts)
        rvPost.adapter = adapterPost

        val profiles = mutableListOf<Profile>()

        val rvProfiles = binding.rvProfiles
        rvProfiles.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        val adapterProfiles = ProfilesAdapter(profiles)
        rvProfiles.adapter = adapterProfiles

        // get all profiles from database

        viewModel.dbAllProfiles.observe(viewLifecycleOwner) { profilesList ->
            // when first viewmodel is init() dbAllProfiles is null
            if (profilesList == null) return@observe
            // for now refresh list
            profiles.removeAll(profiles)
            profiles.addAll(profilesList)
            adapterProfiles.notifyItemRangeChanged(0, profilesList.size)
        }


        viewModel.allPosts.observe(viewLifecycleOwner) { postsList ->
            // when first viewmodel is init() allPosts is null
            if (postsList == null) return@observe
            // for now refresh list
            posts.removeAll(posts)
            posts.addAll(postsList)
            adapterPost.notifyItemRangeChanged(0, postsList.size)
        }





        val d = Draft(
            0,
            "title",
            "description",
            "today" ,
            "",
            "",
        )

        /*
        viewModel.addDraft(d)

        viewModel.allDrafts.observe(viewLifecycleOwner, { draftList ->
            binding.title.text = draftList.first().title
        })
        */

    }


}