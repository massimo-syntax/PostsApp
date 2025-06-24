package com.example.postsapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postsapp.models.Post
import com.google.firebase.Firebase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.database

class PostViewModel : ViewModel() {

    // FIREBASE INSTANCE
    private val firebaseRTDB = Firebase.database


    // single post
    val _currentPost = MutableLiveData<Post?>(null)
    val currentPost : LiveData<Post?>
        get() = _currentPost


    //      GET LIST OF POSTS
    val _postsList = MutableLiveData<MutableList<Post>?>(null)
    val postsList : LiveData<MutableList<Post>?>
        get() = _postsList

    // REQUIRE ONCE LIST OF POSTS
    fun getPostsList(){
        val postsRef = firebaseRTDB.getReference("posts")
        postsRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val snapshot = it.result
                val posts = mutableListOf<Post>()
                for (postsSnapshot in snapshot.children) {
                    val p = postsSnapshot.getValue(Post::class.java)
                    posts.add(p!!)
                }
                //Log.d(TAG, "${profiles.size}")
                _postsList.value = posts
            } else {
                // EROOR
                //Log.d(TAG, "${it.exception?.message}")
            }
        }
    }


    fun getCurrentPost(id:String){
        val currentPostRef = firebaseRTDB.getReference("posts/$id")

        // get current Post
        currentPostRef.get().addOnSuccessListener {
            _currentPost.value = it.getValue(Post::class.java)
        }.addOnFailureListener {
            // failure
        }
    }


    //      CALLBACK FOR OBSERVER SENDING POST
    val _postSentSuccessfully = MutableLiveData<Boolean>(false)
    val postSentSuccessfully : LiveData<Boolean>
        get() = _postSentSuccessfully


    fun post( p:Post , uid:String){
        // let create an id from firebase
        val key = firebaseRTDB.getReference("posts").push().key
        if (key == null) return
        p.id = key
        val updates: MutableMap<String, Any> = hashMapOf(
            "posts/$key" to p,
            "profiles/$uid/postsCount" to ServerValue.increment(1),
        )
        Firebase.database.reference.updateChildren(updates).addOnCompleteListener {
            _postSentSuccessfully.value = true
        }
    }



    fun likePost(currentUID:String){
        // add element to map
        currentPost.value!!.likes!![currentUID] = true
        // get firebase reference for this post / likes
        val postId = currentPost.value!!.id
        firebaseRTDB.getReference("posts/$postId/likes/$currentUID").setValue(true)
        firebaseRTDB.getReference("posts/$postId/likesCount").setValue(ServerValue.increment(1))
    }

    fun unlikePost(currentUID:String){
        // remove key from live data
        currentPost.value!!.likes!!.remove(currentUID)
        val postId = currentPost.value!!.id
        firebaseRTDB.getReference("posts/$postId/likes/$currentUID").removeValue()
        firebaseRTDB.getReference("posts/$postId/likesCount").setValue(ServerValue.increment(-1))

    }


    init{
        // nothing at viewmodel instanciation for now
    }


}