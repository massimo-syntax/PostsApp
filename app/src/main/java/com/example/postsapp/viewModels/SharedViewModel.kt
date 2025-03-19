package com.example.postsapp.viewModels

import android.app.Application
import android.widget.Toast
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
    private val currentUID = firebaseAuth.currentUser?.uid!!

    private val _userId = MutableLiveData<String>(currentUID)
    val userId : LiveData<String>
        get() = _userId

    // FIREBASE AUTH    /END




    // FIREBASE REALTIME DATABASE
    val firebaseRTDB = Firebase.database        // FIREBASE INSTANCE
    val firebaseRTDBReference = Firebase.database.reference




    // Write a message to the database
    val prifileRef = firebaseRTDB.getReference("profiles/$currentUID")

    private val _dbProfileChangeListener = MutableLiveData<Profile>(null)
    val dbProfileChangeListener : LiveData<Profile>
        get() = _dbProfileChangeListener

    fun updateProfile(p:Profile){
        prifileRef.setValue(p)
        // same as
        //firebaseRTDBReference.child("profiles").child(currentUID).setValue(p)
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
                if(profile != null) _dbProfileChangeListener.value = profile!!
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(application , "firebase error: ${error.message}" , Toast.LENGTH_SHORT).show()
            }
        })

    }








}