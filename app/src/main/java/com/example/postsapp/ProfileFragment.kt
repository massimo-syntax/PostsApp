package com.example.postsapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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

        viewModel.userId.observe(viewLifecycleOwner){ uid ->
            binding.tvLog.text = uid
        }

        var p = Profile(
            uid = viewModel.userId.value,
            name = "",
            say = "",
            picture = "",
            followers = listOf(),
            followed = listOf()
        )

        viewModel.dbProfileChangeListener.observe(viewLifecycleOwner){ profile ->
            if (profile == null){
                binding.tvLog.text = "no profile yet"
            } else {
                binding.tvLog.text = profile.name
                p = profile
            }
        }


        binding.btnSend.setOnClickListener {
            p.name = binding.etUserName.text.toString()
            p.say = binding.etSay.text.toString()
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