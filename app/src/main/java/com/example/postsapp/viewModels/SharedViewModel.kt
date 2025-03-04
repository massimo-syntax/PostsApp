package com.example.postsapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.postsapp.models.Draft
import com.example.postsapp.repositories.DraftsRepository
import com.example.postsapp.room.DraftsDB
import kotlinx.coroutines.launch


class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val allDrafts : LiveData<List<Draft>>
    private val repository : DraftsRepository

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



}