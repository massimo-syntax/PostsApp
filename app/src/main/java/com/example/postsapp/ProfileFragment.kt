package com.example.postsapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.postsapp.adapters.ProfilesAdapter
import com.example.postsapp.databinding.FragmentProfileBinding
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.MainViewModel
import com.example.postsapp.viewModels.ProfileViewModel
import com.example.postsapp.viewModels.SharedViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var profileViewmodel: ProfileViewModel
    private lateinit var ctx : Context

    fun t(s:Any){
        Toast.makeText(requireContext(),s.toString(),Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // no params
        }
        ctx = requireContext()
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        profileViewmodel = ViewModelProvider(this)[ProfileViewModel::class.java]
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
            postsCount = 0,
            followersCount = 0,
            followers = mutableMapOf<String,Boolean>(),
            followed =  mutableMapOf<String,Boolean>(),
            likedComments = mutableMapOf<String,String>()
        )

        //      F O L L O W E D
        //  rv
        val rvProfiles = binding.rvProfiles
        rvProfiles.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapterProfiles = ProfilesAdapter(profileViewmodel.followedsList){
                profile ->
            val action = ProfileFragmentDirections.actionProfileFragmentToProfileDetailsFragment(profile.uid!!)
            findNavController().navigate(action)
        }
        rvProfiles.adapter = adapterProfiles


        // wait for followed from databse
        profileViewmodel.eventFollowedReceived.observe(viewLifecycleOwner){
                followed ->
            // in viewmodel starting value is null
            if(followed == null) return@observe
            adapterProfiles.notifyItemInserted(profileViewmodel.followedsList.size-1)
        }
        // get followed is in the observer of myProfile



        // load fields of current profile in ui
        // get followeds when myProfile is loaded
        profileViewmodel.myProfile.observe(viewLifecycleOwner){ profile ->
            if (profile == null) {
                binding.tvTitle.text = "-- ! NO PROFILE YET ! --"
                binding.iv.setImageResource(R.drawable.writing)
                binding.constraintEditSection.visibility = View.VISIBLE
                return@observe
            }
            // print profile info in fields
            p = profile
            binding.tvTitle.text = p.name
            binding.etUserName.setText(p.name)
            binding.etSay.setText(p.say)
            binding.constraintEditSection.visibility = View.GONE
            if(profile.followed.isNullOrEmpty()) binding.followedLable.text = "No profile followed"
            binding.tvFollowersCount.text = p.followersCount.toString()
            binding.tvPostsCount.text = p.postsCount.toString()
            binding.tvWrittenComments.text = p.myComments!!.size.toString()

            // load image if present
            if( ! p.image.isNullOrEmpty() ){
                Glide.with(ctx)
                    .load(p.image)
                    .into(binding.iv)
                image = p.image!!
            }
            // once the profile is loaded retrive followed
            profileViewmodel.getFollowed()
        }


        val vew = binding.constraintEditSection

        binding.btnEdit.setOnClickListener {

            if( vew.isGone ){
                // Prepare the View for the animation

                vew.visibility = View.VISIBLE;
                vew.alpha = 0.5f
                vew.scaleX = 0.7f
                vew.scaleY = 0.7f
                // Start the animation
                vew.animate()
                    .alpha(1.0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setListener(null)
                    .setDuration(100)

            }else{
                vew.animate()
                    .alpha(0.5f)
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .setDuration(100)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            vew.visibility = View.GONE
                        }
                    })
            }

        }


        //      I M A G E  BUTTONS
        // easiest way to upload picture for now
        // UPLOAD
        binding.btnSelectImg.setOnClickListener {
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


        // BTN      S U B M I T
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

    // when another tab is pressed the list is new
    // when this fragment is displayed again the adapter is populated from 0
    // otherwise the viewmodel instance has all properties still the same
    override fun onPause() {
        super.onPause()
        profileViewmodel.followedsList = mutableListOf<Profile>()
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onResume() {
        super.onResume()
        mainViewModel.currentSection = getString( R.string.profile )
        mainViewModel.setActionBarTitle("Your profile")
    }






















}