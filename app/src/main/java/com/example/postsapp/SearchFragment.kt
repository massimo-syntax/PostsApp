package com.example.postsapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postsapp.adapters.SearchAdapter
import com.example.postsapp.databinding.FragmentSearchBinding
import com.example.postsapp.models.Both
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.MainViewModel
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

        //var query = ""

        // for adapter to grab new update of string query, there is need of a pointer

        val query = QueryString()

        // recycler view
        val rvSearch = binding.rvSearch
        // adapter
        val bothList = mutableListOf<Both>()
        val adapterSearch = SearchAdapter(bothList , query){
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
            posts = postsList
        }
        // request posts from databse
        postsViewmodel.getPostsList()


        // click
        // ADD to LIST matching CHECKBOXES
        binding.btnSearch.setOnClickListener {
            val quiry = binding.etSearch.text.toString().trim()
            var bothObj:Both? = null

            // to remove from adapter
            val oldDatasetSize = bothList.size
            // to add notifyItemRangeInserted for adapter
            var newDatasetSize = 0
            // refresh the whole list
            bothList.removeAll(bothList)

            fun truncate(quiry:String , text:String ):String{
                var txt = ""
                val len = 150
                if( text.length > len){
                    txt = text.slice(0..len) + "..."
                    if(text.contains(quiry) && !txt.contains(quiry)) txt += "$quiry..."
                }else{
                    txt = text
                }
            return txt
            }


            // PROFILES CHECKED
            if(binding.checkProfiles.isChecked){
                profiles.forEach{ profile ->
                    if(profile.image.isNullOrEmpty()) profile.image = ""
                    bothObj = Both(
                            id = profile.uid ?: "123",
                            title = profile.name ?: "",
                            image =  profile.image!!,
                            description = profile.say ?: "",
                            count = profile.followersCount ?: 0,
                            dateTime = profile.datetime.toString(),
                            type = "profile"
                        )
                    // add profile if matching query
                    if( bothObj!!.title.contains(quiry) || bothObj!!.description.contains(quiry)){
                        bothList.add(bothObj!!)
                        newDatasetSize++
                    }
                }

            }

            // POSTS CHECKED
            if(binding.checkPosts.isChecked){
                posts.forEach{post ->
                    // integrate tags in descriptions for posts
                    var description = post.body ?: ""

                    description = truncate(quiry, description)

                    if( ! post.tags.isNullOrEmpty() ){
                        description+= "\n"
                        post.tags!!.forEach { tag ->
                            description += "#${tag.key} "
                        }
                        // remove last " "
                        description.dropLast(1)
                    }
                    if(post.image.isNullOrEmpty()) post.image = ""
                    bothObj = Both(
                            id = post.id ?: "123",
                            title = post.title ?: "",
                            image = post.image ?: "",
                            description = description,
                            count = post.likesCount ?: 0,
                            dateTime = post.datetime.toString(),
                            type = "post"
                        )
                    // add if post matching query
                    if( bothObj!!.title.contains(quiry) || bothObj!!.description.contains(quiry)){
                        bothList.add(bothObj!!)
                        newDatasetSize++
                    }
                }
            }
            // completely refresh adapter to the new list
            //
            //
            //
            query.s = quiry
            adapterSearch.notifyItemRangeRemoved(0 , oldDatasetSize)
            adapterSearch.notifyItemRangeInserted(0 , newDatasetSize )
            binding.etSearch.setText("")

            // hide keyboard
            if(requireActivity().currentFocus == null) return@setOnClickListener
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
        }/* click listener submit */
    }/* onViewCreated */


    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        mainViewModel.currentSection = getString( R.string.search )
        mainViewModel.setActionBarTitle("Search")
    }

}