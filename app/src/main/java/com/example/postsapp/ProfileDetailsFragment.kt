package com.example.postsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.postsapp.databinding.FragmentProfileDetailsBinding
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.FragmentStateViewModel
import com.example.postsapp.viewModels.ProfileViewModel

class ProfileDetailsFragment : Fragment() {

    private var profileId:String? = null

    private lateinit var binding : FragmentProfileDetailsBinding
    private lateinit var viewModel: ProfileViewModel
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
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
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

        fun alreadyLiked():Boolean{
            return profile!!.followers!!.containsKey(viewModel.myProfile.value!!.uid.toString())
        }

        var likes = 0

        // init text waiting for server response
        // useful in the case that no profile is created yet
        binding.profileId.text = ""
        binding.profileName.text = ""
        binding.sentence.text = ""
        binding.likes.text = ""

        // REQUESTING PROFILE FROM DB
        viewModel.singleProfile.observe(viewLifecycleOwner){ p ->
            if( p == null) return@observe
            profile = p
            //toast(p.toString())
            likes = profile!!.followersCount!!
            binding.likes.text = likes.toString()
            binding.profileId.text = p.uid
            binding.profileName.text = p.name
            binding.sentence.text = p.say
            binding.likes.text = likes.toString()

            if(alreadyLiked()){
                binding.btnFollow.setImageResource(R.drawable.like)
            }
        }
        // request profile
        viewModel.getSingleProfile(profileId!!)

        // btn like profile
        binding.btnFollow.setOnClickListener {
            if ( ! alreadyLiked() ){
                viewModel.likeProfile(profile!!.uid!!)
                likes ++
                binding.likes.text = likes.toString()
                binding.btnFollow.setImageResource(R.drawable.like)
            }else{
                viewModel.unlikeProfile(profile!!.uid!!)
                likes --
                binding.likes.text = likes.toString()
                binding.btnFollow.setImageResource(R.drawable.unliked)
            }
        }

        // BTN BACK
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    // navigating from the bottom navbar (pressing "home") to the same fragment, is not shown this fragment still on stack but home
    override fun onResume() {
        if(fragmentState.fragmentWasPaused)findNavController().popBackStack()
        super.onResume()
    }

    override fun onPause() {
        fragmentState.fragmentWasPaused = true
        super.onPause()

    }


}