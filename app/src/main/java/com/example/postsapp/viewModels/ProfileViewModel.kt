package com.example.postsapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postsapp.models.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class ProfileViewModel : ViewModel() {


    // FIREBASE AUTH                                                        FIREBASE AUTH
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentUID = firebaseAuth.currentUser?.uid!!

    // FIREBASE AUTH    /END


    // FIREBASE INSTANCE
    val firebaseRTDB = Firebase.database

    // P R O F I L E
    val myProfileRef = firebaseRTDB.getReference("profiles/$currentUID")

    // profile status when no profile
    private val _myProfile = MutableLiveData<Profile?>(null)
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

    //      GET LIST OF PROFILES
    private val _profilesList = MutableLiveData<MutableList<Profile>?>(null)
    val profilesList : LiveData<MutableList<Profile>?>
        get() = _profilesList


    // REQUIRE ONCE LIST OF PROFILES
    fun getProfilesList(){
        val profilesRef = firebaseRTDB.getReference("profiles")
        profilesRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val snapshot = it.result
                val profiles = mutableListOf<Profile>()
                for (profileSnapshot in snapshot.children) {
                    val p = profileSnapshot.getValue(Profile::class.java)
                    profiles.add(p!!)
                }
                //Log.d(TAG, "${profiles.size}")
                _profilesList.value = profiles
            } else {
                // EROOR
                //Log.d(TAG, "${it.exception?.message}")
            }
        }
    }

    // followed rv
    private val _eventFollowedReceived = MutableLiveData<Profile?>(null)
    val eventFollowedReceived : LiveData<Profile?>
        get() = _eventFollowedReceived

    var followedsList = mutableListOf<Profile>()

    fun getFollowed(){
        val followed = _myProfile.value!!.followed
        if (followed==null) return

        followed.forEach { followed ->
            val fID = followed.key
            firebaseRTDB.getReference("profiles/$fID")
                .get()
                .addOnSuccessListener {
                    // firebase does not send null
                    val p = it.getValue(Profile::class.java)!!
                    followedsList.add( p )
                    _eventFollowedReceived.value = p
                }
        }
    }


    fun likeProfile(otherProfileId:String){
        val db = Firebase.database.reference

        val updates: MutableMap<String, Any> = hashMapOf(
            "profiles/$otherProfileId/followers/$currentUID" to true,
            "profiles/$currentUID/followed/$otherProfileId" to true,
            // increment +1 the count of follower in the other profile
            "profiles/$otherProfileId/followersCount" to ServerValue.increment (1)
        )
        db.updateChildren(updates)

        // update the livedata
        _singleProfile.value!!.followers!![currentUID] = true
        _myProfile.value!!.followed!![otherProfileId] = true

        var followersCount = _singleProfile.value!!.followersCount!!
        followersCount++
        _singleProfile.value!!.followersCount = followersCount
    }

    fun unlikeProfile(otherProfileId: String){
        val profilesReference = firebaseRTDB.getReference("profiles")

        // remove follower from databse
        profilesReference.child(otherProfileId).child("followers/$currentUID").removeValue()
        profilesReference.child(otherProfileId).child("followersCount").setValue(ServerValue.increment(-1))

        profilesReference.child(currentUID).child("followed/$otherProfileId").removeValue()

        // update livedata
        _singleProfile.value!!.followers!!.remove(currentUID)
        _myProfile.value!!.followed!!.remove(otherProfileId)

        var followersCount = _singleProfile.value!!.followersCount!!
        followersCount--
        _singleProfile.value!!.followersCount = followersCount
    }



    init {
        // retrive my profile at viewmodel instanciation ..
        // on data change receives the data form firebase also first when started
        // in the case of the profile fragment, to update the profile, is also useful a listener on change
        myProfileRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue<Profile>()
                if(profile != null) _myProfile.value = profile
            }
            override fun onCancelled(error: DatabaseError) {
                // error
            }
        })

    }// init{END}

}