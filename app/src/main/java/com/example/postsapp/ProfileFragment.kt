package com.example.postsapp

import android.R
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.postsapp.databinding.FragmentProfileBinding
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.SharedViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding : FragmentProfileBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var ctx : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        ctx = requireContext()
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var image = ""

        var p = Profile(
            // login is required to access the app
            uid = viewModel.userId.value,
            name = "",
            say = "",
            image = "",
            followers = mutableMapOf<String,Boolean>(),
            followed =  mutableMapOf<String,Boolean>()
        )

        // load fields of current profile in ui
        viewModel.dbProfile.observe(viewLifecycleOwner){ profile ->
            if (profile == null){
                binding.tvTitle.text = "no profile yet"
            } else {
                p = profile
                binding.tvTitle.text = p.name
                binding.etUserName.setText(p.name)
                binding.etSay.setText(p.say)

                if( ! p.image.isNullOrEmpty() ){
                    Glide.with(ctx)
                        .load(p.image)
                        .into(binding.iv)
                    image = p.image!!
                }
            }
        }

        // easiest way to upload picture for now
        // UPLOAD
        binding.btnSelectImg .setOnClickListener {
            AlertDialog.Builder( ctx )
                .setTitle("Upload Pictures")
                .setMessage("SCROLL to ->COPY ALL<- after uploading\nCome back, click CONFIRM! ")
                .setPositiveButton("go!"){_,_ ->
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse("https://uploadimgur.com/")
                    startActivity(openURL)
                }.show()
        }
        // CONFIRM UPLOADING SUCCESS
        binding.btnConfirmImg.setOnClickListener {
            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val abc = clipboard.primaryClip
            val item = abc?.getItemAt(0)
            val url = item?.text.toString().split("\n").first()
            Glide.with(ctx)
                .load(url)
                .into(binding.iv)
            image = url

        }

        // UPDATE !
        binding.btnSend.setOnClickListener {
            if(binding.etUserName.text.isEmpty() ){
                binding.etUserName.setText("PROFILE NAME IS REQUIRED")
                return@setOnClickListener
            }
            p.name = binding.etUserName.text.toString()
            p.say = binding.etSay.text.toString()
            p.image = image
            viewModel.updateProfile(p)
        }

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}