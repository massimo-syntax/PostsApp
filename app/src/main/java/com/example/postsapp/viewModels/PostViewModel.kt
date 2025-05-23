package com.example.postsapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class PostViewModel : ViewModel() {

    // FIREBASE AUTH                                                        FIREBASE AUTH
    //private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //public val currentUID = firebaseAuth.currentUser?.uid!!

    // FIREBASE INSTANCE
    private val firebaseRTDB = Firebase.database

    /*

    // M Y       P R O F I L E
    private val myProfileRef = firebaseRTDB.getReference("profiles/$currentUID")

    // contructor queries my profile
    var myProfile : Profile ? = null
        get() = field

    */

    // single post
    val _currentPost = MutableLiveData<Post?>(null)
    val currentPost : LiveData<Post?>
        get() = _currentPost


    fun getCurrentPost(id:String){
        val currentPostRef = firebaseRTDB.getReference("posts/$id")

        // get current Post
        currentPostRef.get().addOnSuccessListener {
            _currentPost.value = it.getValue(Post::class.java)
        }.addOnFailureListener {
            // failure
        }
    }

    fun likePost(currentUID:String){
        // add element to map
        currentPost.value!!.likes!![currentUID] = true
        // get firebase reference for this post / likes
        val postId = currentPost.value!!.id
        val currentPostRef = firebaseRTDB.getReference("posts/$postId")
        val postLikeRef = currentPostRef.child("likes")
        // send to firebase

        postLikeRef.setValue(currentPost.value!!.likes!!)
    }

    fun unlikePost(currentUID:String){
        // remove key from live data
        currentPost.value!!.likes!!.remove(currentUID)

        // get firebase reference for this post / likes
        val postId = currentPost.value!!.id
        val currentPostRef = firebaseRTDB.getReference("posts/$postId")
        val postLikeRef = currentPostRef.child("likes")
        // send to firebase

        postLikeRef.setValue(currentPost.value!!.likes!!)


    }


    init{
        /*
        // get my profile
        myProfileRef.get().addOnSuccessListener {
            myProfile = it.getValue(Profile::class.java)
        }.addOnFailureListener { /*failure*/ }
         */
    }


}