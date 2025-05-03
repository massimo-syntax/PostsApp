package com.example.postsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.postsapp.databinding.FragmentPostDetailsBinding
import com.example.postsapp.databinding.FragmentProfileDetailsBinding
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel


class PostDetailsFragment : Fragment() {

    private var postId:String? = null

    //private var likes:Int? = null

    private lateinit var binding : FragmentPostDetailsBinding
    private lateinit var viewModel: PostViewModel

    private fun toast(s:String){
        Toast.makeText(context,s, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString("postId")
        }
        viewModel = ViewModelProvider(this)[PostViewModel::class.java]
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

        binding.tvPostId.text = postId

        viewModel.currentPost.observe(viewLifecycleOwner){ post ->
            if(post == null) return@observe
            binding.tvPostTitle.text = post.title + " from " + post.user
            binding.tvPostBody.text = post.body
            binding.tvLikes.text = post.likes.toString()
        }
        viewModel.getCurrentPost(postId!!)

        binding.btnLike.setOnClickListener {
            val myProfile = viewModel.myProfile

            //toast(myProfile!!.uid.toString())

        }
    }


}