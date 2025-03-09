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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class SharedViewModel(application: Application) : AndroidViewModel(application) {

    // FIREBASE AUTH
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userId = MutableLiveData<String>("userid now")
    val userId : LiveData<String>
        get() = _userId


    // ROOM
    val allDrafts : LiveData<List<Draft>>
    private val repository : DraftsRepository

    val _profile = MutableLiveData<Profile>()

    val profile : LiveData<Profile>
        get() = _profile

    // never use a reference to application context in your viewmodel
    init{
        val _dao = DraftsDB.getDatabase(application).getDraftsDao()
        repository = DraftsRepository(_dao)
        allDrafts = repository.allDrafts
    }

    // drafts

    fun deleteDraft (d: Draft) = viewModelScope.launch {
        repository.delete(d)
    }

    fun updateDraft(d: Draft) = viewModelScope.launch {
        repository.update(d)
    }

    fun addDraft(d: Draft) = viewModelScope.launch {
        repository.insert(d)
    }


    // FIREBASE AUTH
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



}