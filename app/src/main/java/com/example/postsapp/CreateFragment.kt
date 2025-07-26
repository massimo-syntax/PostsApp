package com.example.postsapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.postsapp.adapters.PicturesAdapter
import com.example.postsapp.adapters.ProfilesAdapter
import com.example.postsapp.databinding.FragmentCreateBinding
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.viewModels.FragmentStateViewModel
import com.example.postsapp.viewModels.MainViewModel
import com.example.postsapp.viewModels.PostViewModel
import com.example.postsapp.viewModels.ProfileViewModel
import com.example.postsapp.viewModels.SharedViewModel
import java.util.Date


class CreateFragment : Fragment() {

    private lateinit var binding : FragmentCreateBinding

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var postViewModel: PostViewModel
    private lateinit var fragmentState: FragmentStateViewModel

    private lateinit var ctx : Context

    private val INCOMING_PICTURES = "incoming_pictures"
    val pictures = mutableListOf<String>()

    private lateinit var adapterPictures: PicturesAdapter

    fun deletePicture(adapterIndex:Int){
        pictures.removeAt(adapterIndex)
        adapterPictures.notifyItemRemoved(adapterIndex)
    }

    fun picturesRV(pictures : MutableList<String>){
        val rv = binding.rvImages
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapterPictures = PicturesAdapter(pictures){ index ->
            toast("clicked on index $index")
            deletePicture(index)
        }
        rv.adapter = adapterPictures
    }

    private fun toast(s:String){
        Toast.makeText(context,s, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // no arguments
        }
        ctx = requireContext()

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        fragmentState = ViewModelProvider(this)[FragmentStateViewModel::class.java]

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

        //      I M A G E S
        // easiest way to upload picture for now
        binding.btnSelectImg.setOnClickListener {
            // show confirm image layout
            //binding.layoutConfirmImage.visibility = View.VISIBLE
            fragmentState.incomingPictures = Pair(INCOMING_PICTURES,Date().time)


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
            // get clipboard
            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val abc = clipboard.primaryClip
            val item = abc?.getItemAt(0)

            // pictures list
            val urls = item?.text.toString().split(' ')
            pictures.addAll(urls)
            toast(pictures.toString())
            picturesRV(pictures)
            binding.layoutConfirmImage.visibility = View.GONE

        }

        var tagsList = mutableListOf<String>()
        val tagsForm = binding.layoutTagForm

        fun showTagsForm( tagsList:MutableList<String> ){

            // write in text field the tags in the list if present
            var etText = ""
            tagsList.forEach{ tag -> etText += "$tag " }

            binding.etTags.setText(etText)

            tagsForm.visibility = View.VISIBLE;
            tagsForm.alpha = 0.5f
            tagsForm.scaleX = 0.7f
            tagsForm.scaleY = 0.7f
            tagsForm.translationY = 50.0f
            // Start the animation
            tagsForm.animate()
                .translationY(0.0f)
                .alpha(1.0f)
                .scaleX(1f)
                .scaleY(1f)
                .setListener(null)
                .setDuration(150)
        }

        fun hideTagsForm(){
            tagsForm.animate()
                .translationY(50.0f)
                .alpha(0.5f)
                .scaleX(0.9f)
                .scaleY(0.7f)
                .setDuration(100)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        tagsForm.visibility = View.GONE
                    }
                })
        }

        binding.btnAddTags.setOnClickListener {
            if( tagsForm.isGone ){
                showTagsForm(tagsList)
            }else{
                hideTagsForm()
            }
        }

        binding.btnWriteTags.setOnClickListener {
            var tags = binding.etTags.editableText.toString()
            val re = "[^A-Za-z0-9 ]".toRegex()
            tags = re.replace(tags, "")
            tagsList = tags.trim().split(" ").toMutableList()
            var tagsText = ""
            tagsList.toSet().forEach { tag->
                tagsText += "#$tag "
            }
            binding.tvTags.text = tagsText
            // keep no duplicates in the list
            tagsList = tagsList.toSet().toMutableList()
            hideTagsForm()
            // hide keyboard
            if(requireActivity().currentFocus == null) return@setOnClickListener
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
        }

        postViewModel.postSentSuccessfully.observe(viewLifecycleOwner){ post ->
            if (post == null) return@observe
            val action = CreateFragmentDirections.actionCreateFragmentToPostDetailsFragment(post.id!!)
            findNavController().navigate(action)
        }

        //      S U B M I T
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


                if (tagsList.isNotEmpty()){
                    tagsList.forEach{
                        tag->
                        tagsMap[tag] = true
                    }
                }

                toast(tagsMap.toString())

                var images = ""
                // make a string separated by comma of images urls
                if(pictures.isNotEmpty()){
                    pictures.forEach { pic-> images += "$pic," }
                    // remove last comma ','
                    images = images.dropLast(1)
                }

                val post = Post(
                    id = "from firebase",
                    user = profile!!.name,
                    userId = profileViewModel.currentUID,
                    title = binding.etTitle.text.toString(),
                    body = binding.etBody.text.toString(),
                    image = images,
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
        mainViewModel.currentSection = getString( R.string.create )
        mainViewModel.setActionBarTitle("Write your new post")
        if(
            fragmentState.incomingPictures.first == INCOMING_PICTURES
            &&
            fragmentState.incomingPictures.second < fragmentState.incomingPictures.second + 1000 * 5
            ){
            binding.layoutConfirmImage.visibility = View.VISIBLE
        }
    }

}