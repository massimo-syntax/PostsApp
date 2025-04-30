package com.example.postsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.postsapp.databinding.FragmentProfileDetailsBinding
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.ProfileViewModel

class ProfileDetailsFragment : Fragment() {

    private var profileId:String? = null
    private var name:String? = null
    private var sentence:String? = null
    //private var likes:Int? = null

    private lateinit var binding : FragmentProfileDetailsBinding
    private lateinit var viewModel: ProfileViewModel

    private fun toast(s:String){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
            // id = both.id , name = both.title , sentence = both.description, likes =
            profileId = it.getString("profileId")
            name = it.getString("name")
            sentence = it.getString("sentence")
            //likes = it.getInt("likes")
        }

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

        binding.profileId.text = profileId
        binding.profileName.text = name
        binding.sentence.text = sentence
        binding.likes.text = likes.toString()


        viewModel.singleProfile.observe(viewLifecycleOwner){ p ->
            if( p == null) return@observe
            profile = p
            //toast(p.toString())
            likes = profile!!.followers!!.size
            binding.likes.text = likes.toString()
            if(alreadyLiked()){
                binding.btnLike.text = "unlike"
            }
        }

        viewModel.getSingleProfile(profileId!!)

        // like profile
        binding.btnLike.setOnClickListener {
            if ( ! alreadyLiked() ){
                viewModel.likeProfile()
                likes ++
                binding.likes.text = likes.toString()
                binding.btnLike.text = "unlike"
            }else{
                viewModel.unlikeProfile()
                likes --
                binding.likes.text = likes.toString()
                binding.btnLike.text = "like"
            }
        }


    }





/*

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }


    }

    */
}