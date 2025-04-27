package com.example.postsapp.viewModels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postsapp.models.Like
import com.example.postsapp.models.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlin.coroutines.coroutineContext

class ProfileViewModel : ViewModel() {


    // FIREBASE AUTH                                                        FIREBASE AUTH
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUID = firebaseAuth.currentUser?.uid!!

    private val _userId = MutableLiveData<String>(currentUID)
    val userId : LiveData<String>
        get() = _userId
    // FIREBASE AUTH    /END


    // FIREBASE INSTANCE
    val firebaseRTDB = Firebase.database

    // P R O F I L E
    val myProfileRef = firebaseRTDB.getReference("profiles/$currentUID")

    // profile status when no profile
    val _myProfile = MutableLiveData<Profile?>(null)
    val myProfile : LiveData<Profile?>
        get() = _myProfile


    //      GET SINGLE USER
    val _singleProfile = MutableLiveData<Profile?>(null)
    val singleProfile : LiveData<Profile?>
        get() = _singleProfile

    fun getSingleProfile(id:String){
        val singleProfileRef = firebaseRTDB.getReference("profiles/$id")
        singleProfileRef.get().addOnSuccessListener {
            _singleProfile.value = it.getValue(Profile::class.java)
        }.addOnFailureListener {
            // failure
        }
    }

    fun likeProfile(){
        // followers map of referred profile
        val referredProfileFollowers = singleProfile.value!!.followers
        // add element to map
        referredProfileFollowers!!["${myProfile.value!!.uid}"] = true
        // send to firebase
        val referredProfileRef = firebaseRTDB.getReference("profiles/${singleProfile.value!!.uid}")
        referredProfileRef.child("followers").setValue(referredProfileFollowers)

        // followed map of my profile
        val myProfileFollowed = myProfile.value!!.followed
        // add followed to myProfile map
        myProfileFollowed!!["${singleProfile.value!!.uid}"] = true
        // send to firebase
        myProfileRef.child("followed").setValue(myProfileFollowed)

    }


    init {
        // PROFILE LISTENER FIREBASE REALTIME
        myProfileRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue<Profile>()
                if(profile != null) _myProfile.value = profile!!
            }
            override fun onCancelled(error: DatabaseError) {
                // error
            }
        })

    }// init{END}

}