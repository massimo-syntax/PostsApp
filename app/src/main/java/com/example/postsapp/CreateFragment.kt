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
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.postsapp.databinding.FragmentCreateBinding
import com.example.postsapp.viewModels.SharedViewModel
import com.google.android.gms.dynamic.SupportFragmentWrapper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var binding : FragmentCreateBinding
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
        binding = FragmentCreateBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // go to profile fragment if profile is not created yet
        if(viewModel.dbProfileChangeListener.value == null){
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
        }


        // easiest way to upload picture for now
        binding.btnSelectImg .setOnClickListener {
            AlertDialog.Builder( ctx )
                .setTitle("Upload Pictures")
                .setMessage("Scroll to COPY ALL after uploading\nCome back, click done! ")
                .setPositiveButton("go!"){_,_ ->
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

        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}