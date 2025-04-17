package com.example.postsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.postsapp.databinding.FragmentProfileDetailsBinding
import com.example.postsapp.viewModels.SharedViewModel




class ProfileDetailsFragment : Fragment() {

    private var profileId:String? = null
    private var name:String? = null
    private var sentence:String? = null
    private var likes:Int? = null

    private lateinit var binding : FragmentProfileDetailsBinding
    //private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
            // id = both.id , name = both.title , sentence = both.description, likes =
            profileId = it.getString("profileId")
            name = it.getString("name")
            sentence = it.getString("sentence")
            likes = it.getInt("likes")
        }

        //viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
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


        binding.profileId.text = profileId
        binding.profileName.text = name
        binding.sentence.text = sentence
        binding.likes.text = likes.toString()

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