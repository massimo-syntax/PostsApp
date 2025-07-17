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
import com.example.postsapp.databinding.FragmentProfileDetailsBinding
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.FragmentStateViewModel
import com.example.postsapp.viewModels.MainViewModel
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel

class ProfileDetailsFragment : Fragment() {

    private var profileId:String? = null

    private lateinit var binding : FragmentProfileDetailsBinding
    private lateinit var profilesViewModel: ProfileViewModel
    private lateinit var postsViewModel: PostViewModel

    private lateinit var fragmentState:FragmentStateViewModel

    private fun toast(s:String){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show()
    }

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
        binding =  FragmentProfileDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var profile : Profile? = null

        var likes = 0

        // init text waiting for server response
        // useful in the case that no profile is created yet
        binding.profileName.text = ""
        binding.sentence.text = ""
        binding.tvFollowersCount.text = ""


            // declare rv
            // RV FOR POSTS OF THIS UID
            val rvPosts = binding.rvPosts
            rvPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


            val adapterPosts = PostsAdapter(postsViewModel.UIDPostsList){
                    post ->
                val action = ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToPostDetailsFragment(post.id!!)
                findNavController().navigate(action)
            }
            rvPosts.adapter = adapterPosts



        // add to adapter every post queried
        postsViewModel.eventPostReceived.observe(viewLifecycleOwner){
                post ->
            // in viewmodel starting value is null
            if(post == null) return@observe
            // prevent erors, profilesViewModel.singleProfile.observe is fired 2 times
            // even when emptying the list there there is duplicates
            adapterPosts.notifyItemRangeChanged ( 0 , postsViewModel.UIDPostsList.size )
        }
        // when this single profile is loaded from database the posts are also requested
        // see singleProfile.observe ----v


        fun alreadyFollowed():Boolean{
            return profile!!.followers!!.containsKey(profilesViewModel.myProfile.value!!.uid.toString())
        }
        // REQUESTING PROFILE FROM DB

        var everythingGood = true

        profilesViewModel.singleProfile.observe(viewLifecycleOwner){ p ->
            if( p == null) return@observe
            // this observer is called 2 times..
            if(!everythingGood) return@observe
            everythingGood = false

            profile = p
            //toast(p.toString())
            likes = p.followersCount!!
            binding.tvPostsCount.text = p.postsCount.toString() + " posts"
            binding.tvFollowersCount.text = likes.toString()
            binding.profileName.text = p.name
            binding.sentence.text = p.say
            if( ! p.image.isNullOrEmpty() ) {
                Glide.with(requireContext())
                    .load(p.image)
                    .into(binding.ivImage)
            }
            if(alreadyFollowed()){
                binding.btnFollow.setImageResource(R.drawable.like)
            }

            // make a list of posts from this profile
            if (p.myPosts.isNullOrEmpty()) return@observe

            postsViewModel.getPostsFromIDList(p.myPosts!!.keys.toList())

        }
        // request profile
        profilesViewModel.getSingleProfile(profileId!!)


        // btn like profile
        binding.btnFollow.setOnClickListener {
            if ( ! alreadyFollowed() ){
                profilesViewModel.likeProfile(profile!!.uid!!)
                likes ++
                binding.tvFollowersCount.text = likes.toString()
                binding.btnFollow.setImageResource(R.drawable.like)
            }else{
                profilesViewModel.unlikeProfile(profile!!.uid!!)
                likes --
                binding.tvFollowersCount.text = likes.toString()
                binding.btnFollow.setImageResource(R.drawable.unliked)
            }
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

        mainViewModel.setActionBarTitle("Profile details")

    }





}