package com.example.postsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postsapp.adapters.SearchAdapter
import com.example.postsapp.databinding.FragmentSearchBinding
import com.example.postsapp.models.Both
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel


class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var postsViewmodel:PostViewModel

    private fun toast(s:String){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // NO PARAMETERS
        }
        // init viewmodels
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        postsViewmodel = ViewModelProvider(this)[PostViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // assign xml layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recycler view
        val rvSearch = binding.rvSearch
        // adapter
        val bothList = mutableListOf<Both>()
        val adapterSearch = SearchAdapter(bothList){
            // adapter click listener
            both ->
            when(both.type){
                "profile" -> {
                    val action = SearchFragmentDirections.actionSearchFragmentToProfileDetailsFragment(both.id)
                    findNavController().navigate(action)
                }
                "post" -> {
                    val action = SearchFragmentDirections.actionSearchFragmentToPostDetailsFragment(both.id)
                    findNavController().navigate(action)
                }
                else -> toast("Thats not post neither profile")
            }
        }
        rvSearch.layoutManager = LinearLayoutManager(context)
        rvSearch.adapter = adapterSearch

        // GET LIST OF      P R O F I L E S
        var profiles = listOf<Profile>()
        profileViewModel.profilesList.observe(viewLifecycleOwner){
            profilesList ->
            // when first viewmodel is init() dbAllProfiles is null
            if(profilesList == null) return@observe
            // assign pointer to local variable
            profiles = profilesList.toList()
        }
        // get profiles from firebase databse
        profileViewModel.getProfilesList()


        // GET LIST OF      P O S T S
        var posts = listOf<Post>()
        postsViewmodel.postsList.observe(viewLifecycleOwner){
            postsList ->
            // when first viewmodel is init() allPosts is null
            if(postsList == null) return@observe
            // assign pointer to local variable
            posts = postsList
        }
        // request posts from databse
        postsViewmodel.getPostsList()


        // click
        // ADD to LIST matching CHECKBOXES
        binding.btnSearch.setOnClickListener {
            val query: String = binding.etSearch.text.toString()
            var bothObj:Both? = null

            // to remove from adapter
            val oldDatasetSize = bothList.size
            // to add notifyItemRangeInserted for adapter
            var newDatasetSize = 0
            // refresh the whole list
            bothList.removeAll(bothList)

            // PROFILES CHECKED
            if(binding.checkProfiles.isChecked){
                profiles.forEach{ profile ->
                    bothObj = Both(
                            id = profile.uid ?: "123",
                            title = profile.name ?: "",
                            image = profile.image ?: "",
                            description = profile.say ?: "",
                            count = 0,
                            type = "profile"
                        )
                    // add profile if matching query
                    if( bothObj!!.title.contains(query) || bothObj!!.description.contains(query)){
                        bothList.add(bothObj!!)
                        newDatasetSize++
                    }
                }

            }

            // POSTS CHECKED
            if(binding.checkPosts.isChecked){
                posts.forEach{post ->
                    bothObj = Both(
                            id = post.id ?: "123",
                            title = post.title ?: "",
                            image = post.image ?: "",
                            description = post.body ?: "",
                            count = 0,
                            type = "post"
                        )
                    // add if post matching query
                    if( bothObj!!.title.contains(query) || bothObj!!.description.contains(query)){
                        bothList.add(bothObj!!)
                        newDatasetSize++
                    }
                }
            }
            // completely refresh adapter to the new list
            adapterSearch.notifyItemRangeRemoved(0 , oldDatasetSize)
            adapterSearch.notifyItemRangeInserted(0 , newDatasetSize )

            binding.etSearch.setText("")

            /* this works once but then crashes on click */
            // hide keyword
            // Hiding the keyboard from a Fragment
            //val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus()!!.getWindowToken(), 0)

        }/* click listener submit */


    }/* onViewCreated */

}