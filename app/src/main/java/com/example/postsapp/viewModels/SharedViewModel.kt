package com.example.postsapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.postsapp.models.Draft
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
    private val _userId = MutableLiveData<String>("userid now")
    val userId : LiveData<String>
        get() = _userId

    fun getUserId() {
        _userId.value = firebaseAuth.currentUser?.uid !!
    }

    fun userInfo(pr: Profile){
        getUserId()
        viewModelScope.launch {
            // send profile
            // update profile livedata
        }
    }
    // FIREBASE AUTH    /END




    // FIREBASE REALTIME DATABASE                                            FIREBASE REALTIME DATABASE
    private val _firebaseMessage = MutableLiveData<String>("firebase message now")
    val firebaseMessage : LiveData<String>
        get() = _firebaseMessage

    // Write a message to the database
    val database = Firebase.database
    val myRef = database.getReference("message")

    fun messageToFirebase(s:String){
        myRef.setValue(s)
    }
    // FIREBASE REALTIME DATABASE   / END



    // ROOM                                                                     ROOM
    val allDrafts : LiveData<List<Draft>>
    private val repository : DraftsRepository

    val _profile = MutableLiveData<Profile>()
    val profile : LiveData<Profile>
        get() = _profile

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



    init{
        // INIT ROOM
        val _dao = DraftsDB.getDatabase(application).getDraftsDao()
        repository = DraftsRepository(_dao)
        allDrafts = repository.allDrafts

        // FIREBASE WRITE LISTENER to myRef
        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue<String>()
                if(value != null) _firebaseMessage.value = value!!
            }
            override fun onCancelled(error: DatabaseError) {
                _firebaseMessage.value = error.toException().message
            }
        })
    }


}