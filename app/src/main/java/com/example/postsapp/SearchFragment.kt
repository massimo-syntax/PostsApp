package com.example.postsapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postsapp.adapters.SearchAdapter
import com.example.postsapp.databinding.FragmentSearchBinding
import com.example.postsapp.models.Both
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.SharedViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 *
 */


class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding : FragmentSearchBinding
    private lateinit var viewModel: SharedViewModel

    private fun toast(s:String){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(layoutInflater, container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val posts = mutableListOf<Post>()
        val profiles = mutableListOf<Profile>()
        val bothList = mutableListOf<Both>()

        val rvSearch = binding.rvSearch

        val adapterSearch = SearchAdapter(bothList){ both ->

            when(both.type){
                "profile" -> toast("profile")
                "post" -> toast("post")
                else -> toast("none")
            }

        }

        rvSearch.layoutManager = LinearLayoutManager(context)
        rvSearch.adapter = adapterSearch




        // get all profiles from database
        viewModel.dbAllProfiles.observe(viewLifecycleOwner){ profilesList ->
            // when first viewmodel is init() dbAllProfiles is null
            if(profilesList == null) return@observe
            // for now refresh list
            profiles.removeAll(profiles)
            profiles.addAll(profilesList)

        }


        viewModel.allPosts.observe(viewLifecycleOwner){ postsList ->
            // when first viewmodel is init() allPosts is null
            if(postsList == null) return@observe
            // for now refresh list
            posts.removeAll(posts)
            posts.addAll(postsList)
        }


        binding.btnSearch.setOnClickListener {
            val query: String = binding.etSearch.text.toString()
            bothList.removeAll(bothList)
            var bothObj:Both? = null
            if(binding.checkPosts.isChecked){
                profiles.forEach{profile ->
                    bothObj = Both(
                            id = profile.uid ?: "123",
                            title = profile.name ?: "",
                            image = profile.image ?: "",
                            description = profile.say ?: "",
                            count = 0,
                            type = "profile"
                        )
                    // add profile if matching query
                    if( bothObj!!.title.contains(query) || bothObj!!.description.contains(query))
                        bothList.add(bothObj!!)
                }
            }

            if(binding.checkProfiles.isChecked){
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
                    if( bothObj!!.title.contains(query) || bothObj!!.description.contains(query))
                        bothList.add(bothObj!!)
                }
            }

           // bothList.sortByDescending { it.count }
           //adapterSearch.notifyItemRangeChanged(0, both.size)
            adapterSearch.notifyDataSetChanged()

            // hide keyword

            // Hiding the keyboard from a Fragment

            //val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus()!!.getWindowToken(), 0)

        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}