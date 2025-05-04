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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.postsapp.databinding.FragmentCreateBinding
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.SharedViewModel
import java.util.Date


class CreateFragment : Fragment() {

    private lateinit var binding : FragmentCreateBinding
    private lateinit var viewModel: SharedViewModel

    private lateinit var ctx : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // no arguments
        }
        ctx = requireContext()
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
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

        viewModel.dbProfile.observe(viewLifecycleOwner){ p ->
            if(p != null){
                profile = p
                binding.title.text = p.name
            } else {
                binding.title.text = "you need a profile first"
            }
        }


        // easiest way to upload picture for now
        binding.btnSelectImg .setOnClickListener {
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



        viewModel.dbPosted.observe(viewLifecycleOwner){ post ->
            if(post != null) Toast.makeText(ctx, "post sent succesfully", Toast.LENGTH_SHORT).show()
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
                    val tagsList : List<String> = binding.etTags.text.split(" ")
                    // a null
                    if (tagsList.isNotEmpty()){
                    //somewhere
                        tagsList.forEach{
                    // here
                            tagsMap[it.toString()] = true
                        }
                    }
                }

                val post = Post(
                    id = Date().time.toString(),
                    user = profile!!.name,
                    userId = profile!!.uid,
                    title = binding.etTitle.text.toString(),
                    body = binding.etBody.text.toString(),
                    image = image,
                    datetime = Date().time.toString(),
                    tags = tagsMap,
                    likes = mutableMapOf()
                )
                viewModel.post(post)
            }

        }




    }

}