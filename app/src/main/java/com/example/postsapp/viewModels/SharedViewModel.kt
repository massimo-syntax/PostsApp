package com.example.postsapp.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.postsapp.models.Draft
import com.example.postsapp.models.Post
import com.example.postsapp.models.Profile
import com.example.postsapp.repositories.DraftsRepository
import com.example.postsapp.room.DraftsDB
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.launch


class SharedViewModel(application: Application) : AndroidViewModel(application) {

    // FIREBASE AUTH                                                        FIREBASE AUTH
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUID = firebaseAuth.currentUser?.uid!!

    private val _userId = MutableLiveData<String>(currentUID)
    val userId : LiveData<String>
        get() = _userId

    // FIREBASE AUTH    /END




    // FIREBASE REALTIME DATABASE
    val firebaseRTDB = Firebase.database        // FIREBASE INSTANCE
    val firebaseRTDBReference = Firebase.database.reference


    // P R O F I L E
    val prifileRef = firebaseRTDB.getReference("profiles/$currentUID")

    // profile status when no profile
    private val _dbProfile = MutableLiveData<Profile?>(null)
    val dbProfile : LiveData<Profile?>
        get() = _dbProfile


    fun updateProfile(p:Profile){
        prifileRef.setValue(p)
        // same as
        //firebaseRTDBReference.child("profiles").child(currentUID).setValue(p)
    }

    // P O S T
    // just the dbReference, to create queries .child(id).setValue(obj)
    val postsRef = firebaseRTDB.getReference("posts")

    // query for listeners
    val postsQuery = postsRef.orderByChild("uid").limitToFirst(100)


    private val _dbPosted = MutableLiveData<Post?>(null)
    val dbPosted : LiveData<Post?>
        get() = _dbPosted


    private val _allPosts = MutableLiveData<List<Post>> (listOf())
    val allPosts : LiveData<List<Post>>
        get() = _allPosts


    fun post( p:Post ){
        postsRef.child(p.uid.toString()).setValue(p).addOnSuccessListener {
            _dbPosted.value = p
        }.addOnFailureListener {
            // failure
            _dbPosted.value = null
        }
    }

    // FIREBASE REALTIME DATABASE   / END




    // ROOM                                                                     ROOM
    val allDrafts : LiveData<List<Draft>>
    private val repository : DraftsRepository

    // drafts room
    fun deleteDraft (d: Draft) = viewModelScope.launch {
        repository.delete(d)
    }
    fun updateDraft(d: Draft) = viewModelScope.launch {
        repository.update(d)
    }
    fun addDraft(d: Draft) = viewModelScope.launch {
        repository.insert(d)
    }
    //  ROOM    / END


    /*      just in case...
    val myRef = firebaseRTDB.getReference("message")
    private val _firebaseMessage = MutableLiveData<String>("firebase message now")
    val firebaseMessage : LiveData<String>
        get() = _firebaseMessage
    fun messageToFirebase(s:String){
        myRef.setValue(s)
    }
    */

    init{
        // INIT ROOM
        val _dao = DraftsDB.getDatabase(application).getDraftsDao()
        repository = DraftsRepository(_dao)
        allDrafts = repository.allDrafts

        /* just in case
        // FIREBASE WRITE LISTENER to myRef
        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue<String>()
                if(value != null) _firebaseMessage.value = value!!
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(application , "firebase error: ${error.message}" , Toast.LENGTH_SHORT).show()
            }
        })
        */


        // PROFILE LISTENER FIREBASE REALTIME
        prifileRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue<Profile>()
                if(profile != null) _dbProfile.value = profile!!
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(application , "firebase error: ${error.message}" , Toast.LENGTH_SHORT).show()
            }
        })


        // My top posts by number of stars
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val l = mutableListOf<Post>()
                var post : Post? = null
                for (postSnapshot in dataSnapshot.children) {
                    post = postSnapshot.getValue(Post::class.java)
                    // waiting for firebase returning null as part of a list
                    l.add(post!!)
                    Log.e("tag tag" , post.title.toString())
                }
                _allPosts.value = l
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //
                // ...
            }
        })



    }








}