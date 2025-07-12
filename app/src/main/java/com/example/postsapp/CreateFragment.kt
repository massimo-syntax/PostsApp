package com.example.postsapp

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.postsapp.databinding.FragmentCreateBinding
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.MainViewModel
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel
import com.example.postsapp.viewModels.SharedViewModel
import java.util.Date


class CreateFragment : Fragment() {

    private lateinit var binding : FragmentCreateBinding

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var postViewModel: PostViewModel

    private lateinit var ctx : Context

    private fun toast(s:String){
        Toast.makeText(context,s, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // no arguments
        }
        ctx = requireContext()

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBinding.inflate(layoutInflater,container,false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var profile : Profile? =  null
        var image:String = ""

        profileViewModel.myProfile.observe(viewLifecycleOwner){ p ->
            if(p != null){
                profile = p
                binding.title.text = p.name
            } else {
                binding.title.text = "you need a profile first"
            }
        }


        // easiest way to upload picture for now
        binding.btnSelectImg.setOnClickListener {
            AlertDialog.Builder( ctx )
                .setTitle("Upload Pictures")
                .setMessage("Scroll to COPY ALL after uploading\nCome back, click done! ")
                .setPositiveButton("start!"){_,_ ->
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse("https://uploadimgur.com/")
                    startActivity(openURL)
                }.show()
        }


        binding.btnConfirmImg.setOnClickListener {
            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val abc = clipboard.primaryClip
            val item = abc?.getItemAt(0)
            val url = item?.text.toString().split("\n").first()
            Glide.with(ctx)
                .load(url)
                .into(binding.iv)
            Toast.makeText(ctx , item?.text.toString() , Toast.LENGTH_LONG).show()
            if(url.isNotEmpty()) image = url
        }




        postViewModel.postSentSuccessfully.observe(viewLifecycleOwner){ success ->
            if(success) Toast.makeText(ctx, "post sent succesfully", Toast.LENGTH_SHORT).show()
        }


        binding.btnSend.setOnClickListener {
            if( profile == null){
                AlertDialog.Builder(ctx)
                    .setMessage("no profile yet, create a profile")
                    .setPositiveButton("create profile"){_,_ ->
                        //
                        findNavController().navigate(R.id.profileFragment, null, NavOptions.Builder()
                            .setPopUpTo(R.id.createFragment, true)
                            .build())
                        //
                        //
                    }
                    .show()
            }else{
                // title and body are required
                if(binding.etTitle.text.isEmpty()){
                    binding.etTitle.setText("required")
                    return@setOnClickListener
                }
                if(binding.etBody.text.isEmpty()){
                    binding.etBody.setText("required")
                    return@setOnClickListener
                }

                val tagsMap : MutableMap<String,Boolean> = mutableMapOf()

                // probably crashes when null
                if (! binding.etTags.text.isNullOrEmpty()){
                    // there can be
                    val tagsList : List<String> = binding.etTags.text.trim().split(" ")
                    toast(tagsList.toString())
                    // a null
                    if (tagsList.isNotEmpty()){
                    //somewhere
                        tagsList.forEach{
                            tag->
                            tagsMap[tag] = true
                        }
                    }
                    toast(tagsMap.toString())
                }

                val post = Post(
                    id = "from firebase",
                    user = profile!!.name,
                    userId = profileViewModel.currentUID,
                    title = binding.etTitle.text.toString(),
                    body = binding.etBody.text.toString(),
                    image = image,
                    datetime = Date().time.toString(),
                    tags = tagsMap,
                    likes = mutableMapOf<String,Boolean>(),
                    likesCount = 0
                )
                postViewModel.post(post, profileViewModel.currentUID)
            }

        }

    }


    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        mainViewModel.currentSection = getString( R.string.create)
        mainViewModel.setActionBarTitle("Write your new post")
    }

}