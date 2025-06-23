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

    // develpoment
    fun toast(s:String){
        Toast.makeText(getApplication(), s , Toast.LENGTH_SHORT).show()
    }


    // FIREBASE REALTIME DATABASE
    val firebaseRTDB = Firebase.database        // FIREBASE INSTANCE
    val firebaseRTDBRoot = Firebase.database.reference


    // P R O F I L E
    val profileRef = firebaseRTDB.getReference("profiles/$currentUID")

    // profile status when no profile
    private val _dbProfile = MutableLiveData<Profile?>(null)
    val dbProfile : LiveData<Profile?>
        get() = _dbProfile


    fun updateProfile(p:Profile){
        profileRef.setValue(p)
        // same as
        //firebaseRTDBReference.child("profiles").child(currentUID).setValue(p)
    }

    val profilesQuery = firebaseRTDB.getReference("profiles").limitToFirst(50)

    private val _dbAllProfiles = MutableLiveData<List<Profile>?>(null)
    val dbAllProfiles : LiveData<List<Profile>?>
        get() = _dbAllProfiles



    // P O S T
    // just the dbReference, to create queries .child(id).setValue(obj)
    val postsRef = firebaseRTDB.getReference("posts")

    // query for listeners
    val postsQuery = postsRef.orderByChild("id").limitToFirst(100)//.equalTo("queryparameter")


    private val _dbPosted = MutableLiveData<Post?>(null)
    val dbPosted : LiveData<Post?>
        get() = _dbPosted


    private val _allPosts = MutableLiveData<List<Post>?> (null)
    val allPosts : LiveData<List<Post>?>
        get() = _allPosts

    // POSTING A POST
    // for now there is no userid in the post, when user changes profile name all other posts of himself do not appear on his profile
    // query the profilename in posts for now is nice

    // updating just nPost, does not update the entire profile, the listener is for the profile with no bubbling
    // when the fragment is not reloaded, then the viewmodel is the same object firebase is not queried again
    // just added a nPost ++ in a child branch of the listener..
    var nPosts:Int = 0
/*
    fun post( p:Post ){
        postsRef.child(p.id.toString()).setValue(p).addOnSuccessListener {
            _dbPosted.value = p
            // with new viewmodel loaded nPost is 0 again...
            // cannot be set elsewhere, firebase requires time, doesnt work
            // in future can be that update the intire profile changing just a node is also good
            nPosts = _dbProfile.value!!.nPosts!!
            nPosts++
            // that is updated only here.. in the function
            _dbProfile.value!!.nPosts = nPosts
            profileRef.child("nPosts").setValue( nPosts )
        }.addOnFailureListener {
            // failure
            _dbPosted.value = null
        }
    }
*/

    val profilesListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val l = mutableListOf<Profile>()
            var profile : Profile? = null
            for (postSnapshot in dataSnapshot.children) {
                profile = postSnapshot.getValue(Profile::class.java)
                // waiting for firebase returning null as part of a list
                l.add(profile!!)

            }
            Toast.makeText(application.applicationContext,l.toString(),Toast.LENGTH_SHORT).show()
            _dbAllProfiles.value = l
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(application , "firebase error: ${databaseError.message}" , Toast.LENGTH_SHORT).show()
        }
    }


    val postsListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val l = mutableListOf<Post>()
            var post : Post? = null
            for (postSnapshot in dataSnapshot.children) {
                post = postSnapshot.getValue(Post::class.java)
                // waiting for firebase returning null as part of a list
                l.add(post!!)
            }
            Log.e("posts:" , l.toString())
            _allPosts.value = l
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(application , "firebase error: ${databaseError.message}" , Toast.LENGTH_SHORT).show()
        }
    }




    fun startListenersForLists(){
        postsQuery.addValueEventListener(postsListener)
        profilesQuery.addValueEventListener(profilesListener)

    }



    fun removePostsListener(){
        postsQuery.removeEventListener(postsListener)
    }


    fun removeProfilesListener(){
        profilesQuery.removeEventListener(profilesListener)
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
                // no internet
            }
        })
        */


        // CURRENT PROFILE.. IT CAN BE USED THE PROFILE VIEWMODEL, NOW THERE IS
        // FIRST A BIT OF TIDY UP AND HANDLE THE LISTENERS FOR THE LISTS
        profileRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue<Profile>()
                if(profile != null) _dbProfile.value = profile!!
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(application , "firebase error: ${error.message}" , Toast.LENGTH_SHORT).show()
            }
        })

        // just register the listeners as it was before to avoid crashes for now..
        startListenersForLists()





    }








}