package com.example.postsapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.google.firebase.Firebase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.database
import java.util.StringTokenizer

class PostViewModel : ViewModel() {

    // FIREBASE INSTANCE
    private val firebaseRTDB = Firebase.database


    // single post
    private val _currentPost = MutableLiveData<Post?>(null)
    val currentPost: LiveData<Post?>
        get() = _currentPost


    //      GET LIST OF POSTS
    private val _postsList = MutableLiveData<MutableList<Post>?>(null)
    val postsList: LiveData<MutableList<Post>?>
        get() = _postsList

    // REQUIRE ONCE LIST OF POSTS
    fun getPostsList() {
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

    /*
    // REQUIRE ONCE ALL POSTS FROM nyPosts of a UID
    // followed rv
    private val _eventPostReceived = MutableLiveData<Post?>(null)
    val eventPostReceived : LiveData<Post?>
        get() = _eventPostReceived
    var UIDPostsList = mutableListOf<Post>()
    fun getPostsFromIDList( postIDs : List<String> ){
        UIDPostsList.removeAll(UIDPostsList)
        /*
        val count = postIDs.size
        var successReceivedCount = 0
        */
        if (postIDs.isNullOrEmpty() ) return
        postIDs.forEach { postID ->
            firebaseRTDB.getReference("posts/$postID")
                .get()
                .addOnSuccessListener {
                    // firebase does not send null
                    val p = it.getValue(Post::class.java)!!
                    // add at beginning of list
                    UIDPostsList.add (0 , p )
                    _eventPostReceived.value = p
                }
        }
    }
*/

    // to allow reaching .orderByChild("userId").equalTo(id)
    // in firebase itself has to be diefined that in "rules" , of the db

    /*
 "rules": {
    ".read": true, //"now < 1744495200000",  // 2025-4-13
    ".write": true, //"now < 1744495200000",  // 2025-4-13
    "posts": {
      ".indexOn": "userId"
    },
  }
    */
    fun getPostListFromUser(id: String) {
        _postsList.value = null
        val postsRef = firebaseRTDB.getReference("posts").orderByChild("userId").equalTo(id)
        postsRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val snapshot = it.result
                val posts = mutableListOf<Post>()
                for (postsSnapshot in snapshot.children) {
                    val p = postsSnapshot.getValue(Post::class.java)
                    posts.add(p!!)
                }
                _postsList.value = posts
            } else {
                // EROOR
                Log.d("MY POSTS ARE NOT THERE !!", "${it.exception?.message}")
                Log.d("MY POSTS ARE NOT THERE !!", "${it.exception?.message}")
                Log.d("MY POSTS ARE NOT THERE !!", "${it.exception?.message}")
                Log.d("MY POSTS ARE NOT THERE !!", "${it.exception?.message}")
            }
        }
    }

    fun getCurrentPost(id: String) {
        val currentPostRef = firebaseRTDB.getReference("posts/$id")
        currentPostRef.get().addOnSuccessListener {
            _currentPost.value = it.getValue(Post::class.java)
        }.addOnFailureListener {
            // failure
        }
    }


    //      CALLBACK FOR OBSERVER SENDING POST
    private val _postSentSuccessfully = MutableLiveData<Post?>(null)
    val postSentSuccessfully: LiveData<Post?>
        get() = _postSentSuccessfully

    fun post(p: Post, uid: String) {
        // let create an id from firebase
        val key = firebaseRTDB.getReference("posts").push().key
        if (key == null) return
        p.id = key
        val updates: MutableMap<String, Any> = hashMapOf(
            "posts/$key" to p,
            "profiles/$uid/postsCount" to ServerValue.increment(1),
            "profiles/$uid/myPosts/$key" to true
        )
        Firebase.database.reference.updateChildren(updates).addOnSuccessListener {
            _postSentSuccessfully.value = p
        }
        _postSentSuccessfully.value = null
    }

    fun likePost(currentUID: String) {
        // add element to map
        currentPost.value!!.likes!![currentUID] = true
        // get firebase reference for this post / likes
        val postId = currentPost.value!!.id
        firebaseRTDB.getReference("posts/$postId/likes/$currentUID").setValue(true)
        firebaseRTDB.getReference("posts/$postId/likesCount").setValue(ServerValue.increment(1))
    }

    fun unlikePost(currentUID: String) {
        // remove key from live data
        currentPost.value!!.likes!!.remove(currentUID)
        val postId = currentPost.value!!.id
        firebaseRTDB.getReference("posts/$postId/likes/$currentUID").removeValue()
        firebaseRTDB.getReference("posts/$postId/likesCount").setValue(ServerValue.increment(-1))

    }


}